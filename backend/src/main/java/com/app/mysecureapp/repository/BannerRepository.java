package com.app.mysecureapp.repository;
import com.app.mysecureapp.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface BannerRepository extends JpaRepository<Banner, Long> {
    // 獲取所有啟用中並按照順序排好的 Banner
    List<Banner> findByActiveTrueOrderBySortOrderAsc();
}
