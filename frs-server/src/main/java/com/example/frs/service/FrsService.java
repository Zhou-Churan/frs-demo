package com.example.frs.service;

import com.huaweicloud.sdk.frs.v2.FrsClient;
import com.huaweicloud.sdk.frs.v2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FrsService {

    private final FrsClient frsClient;
    private final String faceSetName;

    public FrsService(FrsClient frsClient, @Qualifier("faceSetName") String faceSetName) {
        this.frsClient = frsClient;
        this.faceSetName = faceSetName;
    }

    /**
     * 人脸检测 - 验证图片中是否包含有效人脸
     */
    public DetectFaceByBase64Response detectFace(String imageBase64) {
        DetectFaceByBase64Request request = new DetectFaceByBase64Request();
        FaceDetectBase64Req body = new FaceDetectBase64Req();
        body.withImageBase64(imageBase64);
        body.withAttributes("2,12");
        request.withBody(body);
        return frsClient.detectFaceByBase64(request);
    }

    /**
     * 添加人脸到人脸库
     */
    public AddFacesByBase64Response addFace(String imageBase64, String externalImageId) {
        AddFacesByBase64Request request = new AddFacesByBase64Request();
        request.withFaceSetName(faceSetName);
        AddFacesBase64Req body = new AddFacesBase64Req();
        body.withImageBase64(imageBase64);
        body.withExternalImageId(externalImageId);
        body.withSingle(true);
        request.withBody(body);
        return frsClient.addFacesByBase64(request);
    }

    /**
     * 人脸搜索 - 在人脸库中搜索匹配的人脸
     */
    public SearchFaceByBase64Response searchFace(String imageBase64, double threshold, int topN) {
        SearchFaceByBase64Request request = new SearchFaceByBase64Request();
        request.withFaceSetName(faceSetName);
        FaceSearchBase64Req body = new FaceSearchBase64Req();
        body.withImageBase64(imageBase64);
        body.withThreshold(threshold);
        body.withTopN(topN);
        request.withBody(body);
        return frsClient.searchFaceByBase64(request);
    }

    /**
     * 删除人脸 - 通过faceId删除
     */
    public DeleteFaceByFaceIdResponse deleteFaceByFaceId(String faceId) {
        DeleteFaceByFaceIdRequest request = new DeleteFaceByFaceIdRequest();
        request.withFaceSetName(faceSetName);
        request.withFaceId(faceId);
        return frsClient.deleteFaceByFaceId(request);
    }

    /**
     * 删除人脸 - 通过externalImageId删除
     */
    public DeleteFaceByExternalImageIdResponse deleteFaceByExternalImageId(String externalImageId) {
        DeleteFaceByExternalImageIdRequest request = new DeleteFaceByExternalImageIdRequest();
        request.withFaceSetName(faceSetName);
        request.withExternalImageId(externalImageId);
        return frsClient.deleteFaceByExternalImageId(request);
    }

    /**
     * 更新人脸 - 先删除旧人脸再添加新人脸
     */
    public AddFacesByBase64Response updateFace(String oldFaceId, String imageBase64, String externalImageId) {
        // 先删除旧人脸
        try {
            deleteFaceByFaceId(oldFaceId);
            log.info("成功删除旧人脸: {}", oldFaceId);
        } catch (Exception e) {
            log.warn("删除旧人脸失败: {}, 继续添加新人脸", e.getMessage());
        }
        // 添加新人脸
        return addFace(imageBase64, externalImageId);
    }
}