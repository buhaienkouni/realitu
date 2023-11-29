package ua.kpi.realitu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.Image;
import ua.kpi.realitu.repository.ArticleRepository;
import ua.kpi.realitu.repository.ImageRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Transactional
    public void uploadImageAndSaveToArticle(MultipartFile image, UUID articleId) throws IOException {

        if (imageIsEmpty(image)) {
            return;
        }

        Article article = findArticleById(articleId);
        Image newImage = saveImage(image);

        try {
            Optional<Image> oldImage = Optional.ofNullable(article.getImage());
            article.setImage(newImage);
            articleRepository.save(article);
            oldImage.ifPresent(imageRepository::delete);

        } catch (Exception e) {
            imageRepository.delete(newImage);
            throw new RuntimeException("Image could not be saved to article");
        }
    }

    public Optional<byte[]> getImageDataByArticleId(UUID articleId) {
        return Optional.ofNullable(findArticleById(articleId).getImage())
                .map(Image::getImageData);
    }

    private Image saveImage(MultipartFile image) throws IOException {
        Image newImage = new Image();

        newImage.setImageName(image.getOriginalFilename());
        newImage.setImageType(image.getContentType());
        newImage.setImageData(image.getBytes());
        newImage = imageRepository.save(newImage);

        return newImage;
    }

    private Boolean imageIsEmpty(MultipartFile file) {
        long imageSize = Optional.ofNullable(file)
                .map(MultipartFile::getSize)
                .orElse(0L);

        return imageSize == 0;
    }

    public Article findArticleById(UUID articleId) {
        return articleRepository.findById(articleId).orElseThrow(
                () -> new RuntimeException("Article could not be found by Id: %s".formatted(articleId)));
    }
}
