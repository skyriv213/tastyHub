package com.example.tastyhub.common.domain.user.service;

import static com.example.tastyhub.common.utils.Jwt.JwtUtil.AUTHORIZATION_HEADER;
import static com.example.tastyhub.common.utils.Jwt.JwtUtil.REFRESH_HEADER;

import com.example.tastyhub.common.domain.user.dtos.ChangePasswordRequest;
import com.example.tastyhub.common.domain.user.dtos.UserAuthRequest;
import com.example.tastyhub.common.domain.user.dtos.FindIdRequest;
import com.example.tastyhub.common.domain.user.dtos.SignupRequest;
import com.example.tastyhub.common.domain.user.dtos.UserDto;
import com.example.tastyhub.common.domain.user.dtos.UserNameResponse;
import com.example.tastyhub.common.domain.user.dtos.NicknameDto;
import com.example.tastyhub.common.domain.user.entity.User;
import com.example.tastyhub.common.domain.user.entity.User.userType;
import com.example.tastyhub.common.domain.user.repository.UserRepository;
import com.example.tastyhub.common.domain.village.dtos.LocationRequest;
import com.example.tastyhub.common.domain.village.entity.Village;
import com.example.tastyhub.common.domain.village.service.VillageService;
import com.example.tastyhub.common.utils.Jwt.JwtUtil;
import com.example.tastyhub.common.utils.Redis.RedisUtil;
import com.example.tastyhub.common.utils.S3.S3Uploader;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public static final long REFRESH_TOKEN_TIME = 60 * 60 * 60 * 1000L;

  private final VillageService villageService;


  private final RedisUtil redisUtil;

  private final JwtUtil jwtUtill;

  private final S3Uploader s3Uploader;


  @Override
  @Transactional
  public void checkDuplicatedUsername(String username) {
    if (!userRepository.existsByUsername(username)) {
      return;
    }
    throw new IllegalArgumentException("이미 존재하는 username입니다");
  }

  @Override
  @Transactional
  public void checkDuplicatedNickname(String nickname) {
    if (!userRepository.existsByNickname(nickname)) {
      return;
    }
    throw new IllegalArgumentException("이미 존재하는 nickname입니다");
  }

  @Override
  @Transactional
  public void signup(SignupRequest signupRequest, MultipartFile img) throws java.io.IOException {
    String username = signupRequest.getUserName();
    String encryptedPassword = passwordEncoder.encode(
        signupRequest.getPassword() + username.substring(0, 2)); // 레인보우 테이블을 취약 -> salt 사용을 통해 해결
    String nickname = signupRequest.getNickname();
    String email = signupRequest.getEmail();
    String imgUrl = "https://tastyhub-bucket.s3.ap-northeast-2.amazonaws.com/image/recipeImg/free-icon-user-747376.png"; // 기본 이미지 url

    try {
      if (!img.isEmpty()) {
        imgUrl = s3Uploader.upload(img, "image/userImg");
      }
      User user = User.createUser(username, encryptedPassword, imgUrl, nickname, email,
          userType.COMMON, null);
      userRepository.save(user);
    } catch (Exception e) {
      // 레시피 저장에 실패한 경우, S3에서 이미지 삭제
      if (!imgUrl.isEmpty()) {
        try {
          s3Uploader.delete(imgUrl);
        } catch (IOException ioException) {
          log.error("Failed to delete uploaded image from S3", ioException);
        }
      }
      throw e; // 예외를 다시 던져 트랜잭션 롤백 활성화
    }
  }


  @Override
  public NicknameDto login(UserAuthRequest loginRequest, HttpServletResponse response) {
    String username = loginRequest.getUserName();
    String password = loginRequest.getPassword() + username.substring(0, 2);
    User byUsername = findByUsername(username);
    boolean a = passwordEncoder.matches(password, byUsername.getPassword());
    if (!passwordEncoder.matches(password, byUsername.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지않습니다.");
    }
    String accessToken = jwtUtill.createAccessToken(byUsername.getUsername(),
        byUsername.getUserType());
    String refreshToken = jwtUtill.createRefreshToken(byUsername.getUsername(),
        byUsername.getUserType());

    redisUtil.setDataExpire(REFRESH_HEADER, refreshToken, REFRESH_TOKEN_TIME);
    response.addHeader(AUTHORIZATION_HEADER, accessToken);
    response.addHeader(REFRESH_HEADER, refreshToken);
    return new NicknameDto(byUsername.getNickname());
  }

  @Override
  public UserNameResponse findId(FindIdRequest findIdRequest) {
    User user = findByEmail(findIdRequest);
    String subId = user.getUsername().substring(0, user.getUsername().length() - 4);
    return new UserNameResponse(subId + "****");
  }


  @Override
  @Transactional
  public void changePassword(ChangePasswordRequest changePasswordRequest, User user) {
    User user1 = findByUsername(user.getUsername());
    String password =
        changePasswordRequest.getBeforePassword() + user1.getUsername().substring(0, 2);
    if (!passwordEncoder.matches(password, user1.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지않습니다.");
    }
    user.updatePassword(changePasswordRequest.getChangePassword());
  }

  @Override
  @Transactional
  public Page<UserDto> getUserList(String nickname, Pageable pageable) {
    Page<UserDto> userDtoList = userRepository.findAllByNickname(nickname,pageable);
    return userDtoList;
  }

  @Generated
  private User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지않습니다."));
  }

  @Generated
  private User findByEmail(FindIdRequest findIdRequest) {
    return userRepository.findByEmail(findIdRequest.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
  }

  @Override
  public void delete(UserAuthRequest deleteRequest, User user) throws java.io.IOException {
    String username = deleteRequest.getUserName();
    String password = deleteRequest.getPassword() + username.substring(0, 2);
    String imgUrl = user.getUserImg();

    boolean isCorrectedPassword = passwordEncoder.matches(password, user.getPassword());
    if (!isCorrectedPassword) {
      throw new IllegalArgumentException("비밀번호가 일치하지않습니다.");
    }
    userRepository.delete(user);
    s3Uploader.delete(imgUrl);
  }

  @Override
  @Transactional
  public void updateUserInfoByUserUpdateRequest(NicknameDto nicknameDto,
      MultipartFile img, User user) throws java.io.IOException {
    String imgUrl = "";

    if (!img.isEmpty()) {
      imgUrl = s3Uploader.upload(img, "image/userImg");
    }
    try {
      User findUser = userRepository.findByUsername(user.getUsername())
          .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
      findUser.updateUserInfo(nicknameDto, imgUrl);
    } catch (Exception e) {
      handleUpdateFailure(imgUrl, e);
    }
  }

  @Override
  @Transactional
  public void setVillage(LocationRequest locationRequest, String username) {
    Village village = villageService.getVillage(locationRequest);
    User findUser = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
    findUser.updateVillage(village);
    System.out.println(findUser.getVillage().getAddressTownName());

  }

  private void handleUpdateFailure(String imgUrl, Exception e) throws java.io.IOException {
    if (!imgUrl.isEmpty()) {
      try {
        s3Uploader.delete(imgUrl);
      } catch (IOException ioException) {
        log.error("Failed to delete uploaded image from S3", ioException);
      }
    }
    // 예외를 다시 던져 트랜잭션 롤백 또는 상위 계층에서의 처리를 활성화
    throw e instanceof IOException ? (IOException) e : new RuntimeException(e);
  }

}