package com.app.mysecureapp.repository;

import com.app.mysecureapp.model.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 圖片檔案的資料存取層 (Repository)
 * 這是一個介面，繼承 Spring Data JPA 的 JpaRepository。
 * 只要繼承了它，Spring 就會自動幫我們實作基本的 CRUD (增、刪、改、查) 功能。
 *
 * @param <ImageFile> 這個 Repository 是用來操作哪個 Entity 的。
 * @param <Long> 這個 Entity 的主鍵 (ID) 是什麼型別。
 */
@Repository // 雖然 JpaRepository 已經有 @Repository，但明確寫出來可以增加可讀性
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    // Spring Data JPA 的魔法之處：
    // 你不需要寫任何 SQL 或實作。
    // 例如，如果你未來需要用檔名來找圖片，你只需要定義一個方法：
    // Optional<ImageFile> findByFileName(String fileName);
    // Spring 就會自動幫你產生對應的查詢。

}