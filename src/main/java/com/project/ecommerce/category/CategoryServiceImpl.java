package com.project.ecommerce.category;

import com.project.ecommerce.exceptionhandler.APIException;
import com.project.ecommerce.exceptionhandler.ResourceNotFoundException;
import com.project.ecommerce.util.PageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService
{

    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo)
    {
        this.categoryRepo = categoryRepo;
    }

    public CategoryDTO addCategory(CategoryDTO dto)
    {
        String categoryName = Optional.ofNullable(dto.categoryName)
                                    .map(String::trim)
                                    .orElseThrow(() -> new APIException("Category Name is required!"));

        if(categoryRepo.findByCategoryNameIgnoreCase(categoryName) != null)
        {
            throw new APIException("Category name " + categoryName + "already exists!");
        }

        Category savedCategory = categoryRepo.save(new Category(dto.categoryName));

        return convertToDTO(savedCategory);
    }

    public PageDetails<CategoryDTO> listAllCategories(int pageNum, int pageSize, String sortBy, String sortOrder)
    {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepo.findAll(pageDetails);

        if(categoryPage.isEmpty())
        {
            throw new APIException("No categories available!");
        }

        List<CategoryDTO> categoryDTOS = categoryPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        return new PageDetails<>(
                categoryDTOS,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }

    public void removeCategory(long categoryId)
    {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "category id", categoryId
                ));

        categoryRepo.delete(category);
    }

    public CategoryDTO updateCategory(CategoryDTO dto)
    {
        String categoryName = Optional.ofNullable(dto.categoryName)
                .map(String::trim)
                .orElseThrow(() -> new APIException("Category name required!"));

        Category categoryObj = categoryRepo.findById(dto.id)
                .orElseThrow(
                () -> new ResourceNotFoundException(
                        "Category", "category id", dto.id
                )
        );

        Category existingCategory = categoryRepo.findByCategoryNameIgnoreCase(dto.categoryName);

        if (existingCategory != null && existingCategory.getId() != dto.id) {
            //updated category name is same as the other category which is already present.
            throw new APIException("Category name '" + dto.categoryName + "' already exists!!");
        }

        categoryObj.setCategoryName(dto.categoryName);

        return convertToDTO(categoryRepo.save(categoryObj));
    }

    public boolean isValidCategory(long categoryId)
    {
        return categoryRepo.existsById(categoryId);
    }

    private CategoryDTO convertToDTO(Category category)
    {
        return new CategoryDTO(
                category.getId(),
                category.getCategoryName()
        );
    }

    public Optional<Category> getCategoryObj(long categoryId)
    {
        return categoryRepo.findById(categoryId);
    }
}
