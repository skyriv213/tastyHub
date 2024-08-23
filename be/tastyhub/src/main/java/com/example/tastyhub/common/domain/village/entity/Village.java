package com.example.tastyhub.common.domain.village.entity;

import com.example.tastyhub.common.utils.TimeStamped;
import com.example.tastyhub.common.domain.user.entity.User;
import com.example.tastyhub.common.domain.village.dtos.LocationRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "villages")
public class Village extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "village_id")
    private long id;


    private String addressTownName;

    private double lat; //위도

    private double lng; //경도


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id") // 외래 키 설정
    private User user;

    public static Village createVillage(String addressTownName, double lat, double lng, User user) {
        return Village.builder()
                .addressTownName(addressTownName)
                .lat(lat)
                .lng(lng)
                .user(user)
                .build();
    }

    public void update(LocationRequest locationRequest, String addressTownName) {
        this.addressTownName = addressTownName;
        this.lat = locationRequest.getLat();
        this.lng = locationRequest.getLng();
    }
}
