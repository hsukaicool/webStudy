package com.app.mysecureapp.service;


import com.app.mysecureapp.model.Banner;
import com.app.mysecureapp.repository.BannerRepository;
import com.app.mysecureapp.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

import com.app.mysecureapp.repository.UserRepository;
import com.app.mysecureapp.model.User; // 請確認這是你實際的 User Entity 路徑


@Service
public class BannerService {
    private final BannerRepository bannerRepository;
    private final FileUploadUtil fileUploadUtil;
    private final UserRepository userRepository;

    public BannerService(BannerRepository bannerRepository,
                         FileUploadUtil fileUploadUtil,
                         UserRepository userRepository) {
        this.bannerRepository = bannerRepository;
        this.fileUploadUtil = fileUploadUtil;
        this.userRepository = userRepository;
    }

    public List<Banner> getAllActiveBanners() {
        return bannerRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    @Transactional
    public Banner uploadBanner(String username, MultipartFile file, String title, String link) throws IOException {


        String imageUrl = fileUploadUtil.uploadFile(file, "banners/");

        Banner banner = new Banner();
        banner.setImageUrl(imageUrl);
        banner.setTitle(title);
        banner.setLinkUrl(link);
        banner.setActive(true);
        banner.setSortOrder(0); // 預設順序

        return bannerRepository.save(banner);
    }
}
