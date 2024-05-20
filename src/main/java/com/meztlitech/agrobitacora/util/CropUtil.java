package com.meztlitech.agrobitacora.util;

import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

@Component
@RequiredArgsConstructor
public class CropUtil {

    private final CropRepository cropRepository;
    private final JwtService jwtService;


    public Claims validateCropByUser(String token, Long cropId) {
        Claims claims = jwtService.decodeToken(token);
        Long userId = Long.parseLong(claims.get("id").toString());
        if (!cropRepository.findByIdAndUserId(cropId, userId).isPresent()) {
            throw new HttpServerErrorException(HttpStatus.PRECONDITION_FAILED, "The Crop can't be use by this user");
        }
        return claims;
    }
}
