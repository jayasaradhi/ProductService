package dev.jaya.productservice.services;

import dev.jaya.productservice.dtos.FakeStoreProductDto;
import dev.jaya.productservice.exceptions.ProductNotFoundException;
import dev.jaya.productservice.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
@Service("fakeStoreProductService")
public class FakeStoreProductService implements ProductService {

    private RestTemplate restTemplate;
    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Product getSingleProduct(Long productId) throws ProductNotFoundException {
        ResponseEntity<FakeStoreProductDto> fakeStoreProductResponse = restTemplate.getForEntity(
                "https://fakestoreapi.com/products/" + productId,
                FakeStoreProductDto.class
        );
        FakeStoreProductDto fakeStoreProduct = fakeStoreProductResponse.getBody();
        if (fakeStoreProduct == null) {
            throw new ProductNotFoundException("Product with id: " + productId + " doesn't exist. Retry some other product.");
        }
        return fakeStoreProduct.toProduct();
    }

    @Override
    public List<Product> getProducts() throws ProductNotFoundException{
        FakeStoreProductDto[] products = restTemplate.getForObject(
                "https://fakestoreapi.com/products",
                FakeStoreProductDto[].class
        );
        return Arrays.stream(products).map(FakeStoreProductDto::toProduct)
                .toList();
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


}
