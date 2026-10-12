package com.elkabani.mydb;

import com.elkabani.mydb.entities.Category;
import com.elkabani.mydb.entities.Product;
import com.elkabani.mydb.repositories.CategoryRepository;
import com.elkabani.mydb.repositories.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

@SpringBootApplication
public class MyDbApplication {

    public static void main(String[] args) {
        ApplicationContext context =
                SpringApplication.run(MyDbApplication.class, args);

        var categories = context.getBean(CategoryRepository.class);
        var products = context.getBean(ProductRepository.class);

        // Run the demonstration only when explicitly requested.
        if (!java.util.Arrays.asList(args).contains("--catalog-demo")) {
            System.out.println(
                    "Use --catalog-demo to run operations A, B, and C.");
            return;
        }

        // A: Save a new category and a product belonging to it.
        Category category = new Category();
        category.setName("Electronics");
        category = categories.save(category);

        Product camera = new Product();
        camera.setName("Camera");
        camera.setPrice(new BigDecimal("499.99"));
        camera.setCategory(category);
        camera = products.save(camera);

        System.out.println("\n=== Operation A ===");
        System.out.println("Saved category: " + category.getName()
                + " | ID: " + category.getId());
        System.out.println("Saved product: " + camera.getName()
                + " | ID: " + camera.getId()
                + " | Category ID: " + category.getId());

        // B: Retrieve and reuse the category saved in operation A.
        Category existingCategory = categories.findById(category.getId())
                .orElseThrow(() ->
                        new IllegalStateException("Category not found"));

        Product headphones = new Product();
        headphones.setName("Headphones");
        headphones.setPrice(new BigDecimal("79.99"));
        headphones.setCategory(existingCategory);
        headphones = products.save(headphones);

        System.out.println("\n=== Operation B ===");
        System.out.println("Saved product: " + headphones.getName()
                + " | ID: " + headphones.getId()
                + " | Category ID: " + existingCategory.getId());
        System.out.println("Camera and Headphones share a category: "
                + camera.getCategory().getId()
                .equals(headphones.getCategory().getId()));

        // C: Delete only Headphones and verify the remaining records.
        Long headphonesId = headphones.getId();

        System.out.println("\n=== Operation C ===");
        System.out.println("Before deletion: Headphones exists = "
                + products.existsById(headphonesId));

        products.deleteById(headphonesId);

        System.out.println("After deletion: Headphones exists = "
                + products.existsById(headphonesId));
        System.out.println("Electronics category still exists = "
                + categories.existsById(category.getId()));
        System.out.println("Camera still exists = "
                + products.existsById(camera.getId()));
    }
}