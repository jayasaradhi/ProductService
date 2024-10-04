package dev.jaya.productservice.services;

import dev.jaya.productservice.dtos.FakeStoreProductDto;
import dev.jaya.productservice.exceptions.ProductNotFoundException;
import dev.jaya.productservice.models.Category;
import dev.jaya.productservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("fakeStoreProductService")
public class FakeStoreProductService implements ProductService {

    private RestTemplate restTemplate;
    private RedisTemplate redisTemplate;

    public FakeStoreProductService(RestTemplate restTemplate, RedisTemplate redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Product getSingleProduct(Long productId) throws ProductNotFoundException {
        Product productFromRedis = (Product) redisTemplate.opsForHash().get("PRODUCTS", "PRODUCTS_"+productId);
        if(productFromRedis != null) {
            return productFromRedis;
        }
        ResponseEntity<FakeStoreProductDto> fakeStoreProductResponse = restTemplate.getForEntity(
                "https://fakestoreapi.com/products/" + productId,
                FakeStoreProductDto.class
        );
        FakeStoreProductDto fakeStoreProduct = fakeStoreProductResponse.getBody();
        if (fakeStoreProduct == null) {
            throw new ProductNotFoundException("Product with id: " + productId + " doesn't exist. Retry some other product.");
        }
        redisTemplate.opsForHash().put("PRODUCTS", "PRODUCTS_"+productId, fakeStoreProduct.toProduct());
        return fakeStoreProduct.toProduct();
    }

    @Override
    public Page<Product> getProducts(int pageNumber, int pageSize, String fieldName) throws ProductNotFoundException{
//        FakeStoreProductDto[] products = restTemplate.getForObject(
//                "https://fakestoreapi.com/products",
//                FakeStoreProductDto[].class
//        );
//        return Arrays.stream(products).map(FakeStoreProductDto::toProduct)
//                .toList();
        return null;
    }



    @Override
    public Product createProduct(String title, String description, String category, double price, String image) {
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setTitle(title);
        fakeStoreProductDto.setCategory(category);
        fakeStoreProductDto.setPrice(price);
        fakeStoreProductDto.setImage(image);
        fakeStoreProductDto.setDescription(description);

        FakeStoreProductDto response = restTemplate.postForObject(
                "https://fakestoreapi.com/products", // url
                fakeStoreProductDto, // request body
                FakeStoreProductDto.class // data type of response
        );


        return response.toProduct();
    }

    @Override
    public Product deleteSingleProduct(Long productId) {
        FakeStoreProductDto fakeStoreProduct = restTemplate.getForObject(
                "https://fakestoreapi.com/products/" + productId,
                FakeStoreProductDto.class
        );
        return fakeStoreProduct.toProduct();
    }

    @Override
    public List<String> getCategories() {
        String[] categories = restTemplate.getForObject(
                "https://fakestoreapi.com/products/categories",
                String[].class
        );
        return Arrays.asList(categories);
    }

    @Override
    public Product updateProduct(Long productId, String title, String description, String category, double price, String image) throws ProductNotFoundException {
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setTitle(title);
        fakeStoreProductDto.setCategory(category);
        fakeStoreProductDto.setPrice(price);
        fakeStoreProductDto.setImage(image);
        fakeStoreProductDto.setDescription(description);

        restTemplate.put(
                "https://fakestoreapi.com/products/" + productId,
                fakeStoreProductDto
        );
        return getSingleProduct(productId);

    }

    @Override
    public List<Product> getProductsInCategory(String title) {
        String url = "https://fakestoreapi.com/products/category/" + title;

        FakeStoreProductDto[] fakeStoreProducts = restTemplate.getForObject(url, FakeStoreProductDto[].class);

        if (fakeStoreProducts == null || fakeStoreProducts.length == 0) {
            return List.of();
        }

        return Arrays.stream(fakeStoreProducts)
                .map(FakeStoreProductDto::toProduct)
                .collect(Collectors.toList());
    }


}
