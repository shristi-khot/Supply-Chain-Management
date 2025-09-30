package jsp.spring_boot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jsp.spring_boot.dao.ProductDao;
import jsp.spring_boot.dto.ResponseStructure;
import jsp.spring_boot.entity.Product;
import jsp.spring_boot.entity.Supplier;
import jsp.spring_boot.exception.IdNotFoundException;
import jsp.spring_boot.exception.NoRecordAvailableException;
import jsp.spring_boot.repository.SupplierRepository;

@Service
public class ProductService {

	@Autowired
	private SupplierRepository supplierRepository;
	
	@Autowired
	private ProductDao productDao;

	public ResponseEntity<ResponseStructure<Product>> addProduct( Product product){
		ResponseStructure<Product> response = new ResponseStructure<Product>();
		
		//fetch supplier entity
		Optional<Supplier> supplier = supplierRepository.findById(product.getSupplier().getId());
		if(supplier.isPresent()) {
			product.setSupplier(supplier.get());
			
			
			response.setStatusCode(HttpStatus.CREATED.value());
			response.setMessage("Product added successfully");
			response.setData(productDao.addProduct(product));
			return new ResponseEntity<ResponseStructure<Product>>(response,HttpStatus.CREATED);
		}
		else
			throw new IdNotFoundException("Cannot add Product since Supplier with ID " + product.getSupplier().getId() + " was not found");
		
	}
	
	public ResponseEntity<ResponseStructure<Product>> getProductById( Integer id){
		ResponseStructure<Product> response = new ResponseStructure<Product>();
		Optional<Product> opt = productDao.getProductById(id);
		if(opt.isPresent()) {
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Product with id "+id+" Fetched successfully");
			response.setData(opt.get());
			return new ResponseEntity<ResponseStructure<Product>>(response,HttpStatus.OK);
		}
		else
			throw new IdNotFoundException("No Product Found since id "+id+" is invalid...!!");		
	}
	
	public ResponseEntity<ResponseStructure<List<Product>>> getAllProducts(){
		ResponseStructure<List<Product>> response = new ResponseStructure<List<Product>>();
		List<Product> products = productDao.getAllProducts();
		if(products.isEmpty())
			throw new NoRecordAvailableException("No Products found...!");
		
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Products fetched successfully");
		response.setData(products);
		return new ResponseEntity<ResponseStructure<List<Product>>>(response,HttpStatus.OK);
	}
	
	
	public ResponseEntity<ResponseStructure<Product>> updateProduct( Product product){
		ResponseStructure<Product> response = new ResponseStructure<Product>();
		
		Optional<Product> existingProduct = productDao.getProductById(product.getId());
		
		if(existingProduct.isPresent()) {
			Product newProduct = existingProduct.get();
			newProduct.setName(product.getName());
			newProduct.setPrice(product.getPrice());
			newProduct.setStackQuantity(product.getStackQuantity());
			
			Optional<Supplier> supplier = supplierRepository.findById(product.getSupplier().getId());
			newProduct.setSupplier(supplier.get());
						
			response.setStatusCode(HttpStatus.CREATED.value());
			response.setMessage("Product has been Updated successfully");
			response.setData(productDao.addProduct(newProduct));
			return new ResponseEntity<ResponseStructure<Product>>(response,HttpStatus.CREATED);
		}
		else
			throw new IdNotFoundException("Couldn't update the Product since id "+product.getId()+" is invalid");
	}
	
	public ResponseEntity<ResponseStructure<String>> deleteProduct(Integer id){
		ResponseStructure<String> response = new ResponseStructure<>();
		Optional<Product> opt = productDao.getProductById(id);
		if(opt.isPresent()) {
			productDao.deleteProduct(id);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Product data Deleted successfully");
			response.setData("Product with id "+id+" has been deleted");
			return new ResponseEntity<ResponseStructure<String>>(response,HttpStatus.OK);
		}
		else
			throw new IdNotFoundException("No Product data Found since id " +id+" is invalid....!");
	}
	
	public ResponseEntity<ResponseStructure<List<Product>>> getProductsBySupplier(Integer id){
		ResponseStructure<List<Product>> response = new ResponseStructure<>();
		
		List<Product> products = productDao.getProductsBySupplier(id);
		
		if(products.isEmpty()) {
			throw new IdNotFoundException("No products found for the Supplier id: " +id);
		}
		else
		{
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Record fetched successfully");
			response.setData(products);
			return new ResponseEntity<ResponseStructure<List<Product>>>(response,HttpStatus.OK);
		}	
	}
	
	public ResponseEntity<ResponseStructure<List<Product>>> getProductsByStackQuantity(int quantity){
		ResponseStructure<List<Product>> response = new ResponseStructure<>();

		List<Product> products = productDao.getProductByStackQuantity(quantity);
		if(products.isEmpty()) {
			throw new NoRecordAvailableException("No Products available for the quantity");
		}
		else {
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Products fetched successfully");
			response.setData(products);
			return new ResponseEntity<ResponseStructure<List<Product>>>(response,HttpStatus.OK);
		}
	}
	
	public ResponseEntity<ResponseStructure<Page<Product>>> getProductByPaginationAndSorting( int pageNumber, int pageSize, String name){
		ResponseStructure<Page<Product>> response = new ResponseStructure<>();
		Page<Product> product = productDao.getProductByPaginationAndSorting(pageNumber, pageSize, name);
		
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Products fetched successfully with pagination and sorting");
		response.setData(product);
		
		return new ResponseEntity<ResponseStructure<Page<Product>>>(response,HttpStatus.OK);

	}	
	
}
