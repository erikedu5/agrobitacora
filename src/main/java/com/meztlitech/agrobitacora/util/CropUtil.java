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
        Object roleObj = claims.get("role");
        String role = null;
        if (roleObj instanceof java.util.Map) {
            java.util.Map<?,?> map = (java.util.Map<?,?>) roleObj;
            Object name = map.get("name");
            if (name != null) role = name.toString();
        } else if (roleObj != null) {
            role = roleObj.toString();
        }
        if (!"Ingeniero".equals(role)) {
            Long userId = Long.parseLong(claims.get("id").toString());
            if (!cropRepository.findByIdAndUserId(cropId, userId).isPresent()) {
                throw new HttpServerErrorException(HttpStatus.PRECONDITION_FAILED, "The Crop can't be use by this user");
            }
        }
        return claims;
    }
}
