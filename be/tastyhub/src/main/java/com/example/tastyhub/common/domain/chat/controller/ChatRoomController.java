package com.example.tastyhub.common.domain.chat.controller;

import static com.example.tastyhub.common.config.APIConfig.CHATTING_API;
import static com.example.tastyhub.common.utils.HttpResponseEntity.DELETE_SUCCESS;
import static com.example.tastyhub.common.utils.HttpResponseEntity.RESPONSE_CREATED;
import static com.example.tastyhub.common.utils.HttpResponseEntity.RESPONSE_OK;

import com.example.tastyhub.common.domain.chat.dtos.ChatDto;
import com.example.tastyhub.common.domain.chat.dtos.ChatRoomDto;
import com.example.tastyhub.common.domain.chat.dtos.CheckDto;
import com.example.tastyhub.common.domain.chat.service.ChatRoomService;
import com.example.tastyhub.common.dto.StatusResponse;
import com.example.tastyhub.common.utils.Jwt.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(CHATTING_API)
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /***
     * 채팅방 생성하기
     * @param postId
     * @param userDetails
     * @return RESPONSE_OK
     */
    @PostMapping("/{postId}")
    public ResponseEntity<StatusResponse> createChatRoom(@PathVariable Long postId,
        @AuthenticationPrincipal
        UserDetailsImpl userDetails) {
        chatRoomService.createChatRoom(postId, userDetails.getUser());
        return RESPONSE_CREATED;
    }

    /***
     * 참여하는 채팅방 리스트 조회하기
     * @param userDetails
     * @return List<ChatRoomDto>
     */
    @GetMapping
    public ResponseEntity<Page<ChatRoomDto>> getChatRoomList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
        ){
        Page<ChatRoomDto> chatRoomDtoList = chatRoomService.getChatRoomList(userDetails.getUser(),pageable);
        return ResponseEntity.ok().body(chatRoomDtoList);
    }

    /***
     * 채팅방 입장하기
     * @param
     * @param userDetails
     * @return List<ChatDto> chatDtoList
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatDto>> getChatRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<ChatDto> chatDtoList = chatRoomService.getChatRoom(roomId, userDetails.getUser());

        return ResponseEntity.ok().body(chatDtoList);

    }

    /***
     * 새로운 채팅방 입장하기
     * @param roomId
     * @param userDetails
     * @return RESPONSE_OK
     */
    @PatchMapping("/{roomId}")
    public ResponseEntity<StatusResponse> enterNewChatRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.enterNewChatRoom(roomId, userDetails.getUser());
        return RESPONSE_OK;
    }
//https://localhost/room/{roomId} ->Patch/
    /***
     * 채팅방 나가기
     * @param roomId
     * @param userDetails
     * @return RESPONSE_OK
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<StatusResponse> outChatRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.outChatRoom(roomId, userDetails.getUser());
        return DELETE_SUCCESS;
    }

    /***
     * 채팅방 삭제하기
     * @param roomId
     * @param postId
     * @param userDetails
     * @return RESPONSE_OK
     */
    @DeleteMapping("/{roomId}/{postId}")
    public ResponseEntity<StatusResponse> deleteChatRoom(@PathVariable Long roomId,
        @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.deleteChatRoom(roomId, postId, userDetails.getUser());
        return DELETE_SUCCESS;
    }

    @GetMapping("/check/{postId}")
    public ResponseEntity<CheckDto> checkRoomCondition(@PathVariable Long postId) {
        CheckDto checkDto = chatRoomService.checkRoomCondition(postId);
        return ResponseEntity.ok().body(checkDto);
    }
}
