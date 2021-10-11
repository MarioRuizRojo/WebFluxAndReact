package com.springboot.webflux.restapi.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.springboot.webflux.restapi.app.Utils;
import com.springboot.webflux.restapi.app.models.documents.Category;
import com.springboot.webflux.restapi.app.models.documents.Product;
import com.springboot.webflux.restapi.app.models.services.MyService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.*;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * 
 * @author Mario Ruiz Rojo
 * <br/>
 * Request hanlder for every REST endpoint
 * <br/>
 * All functions to manage Products
 */
@Component
public class ProductHandler {
	
	@Autowired
	private MyService myService;
	
	@Autowired
	private Validator validator;
	
	@Value("${config.pictures.uploads.path}")
	private String picturesUploadsPath;
	
	@Value("${config.urlProducts}")
	private String urlProducts;
	
	@Value("${config.paramProductId}")
	private String paramProductId;
	
	@Value("${config.paramProductName}")
	private String paramProductName;
	
	@Value("${config.paramProductPrice}")
	private String paramProductPrice;
	
	@Value("${config.paramProductCategoryId}")
	private String paramProductCategoryId;
	
	@Value("${config.paramProductCategoryName}")
	private String paramProductCategoryName;
	
	@Value("${config.paramProductFileName}")
	private String paramProductFileName;
	
	/**
	 * It runs all validations annotated in Product.class and throws errors if validation fails
	 * @param product to validate
	 * @return stream with errors if product is not valid
	 */
	private Mono<Product> validateProduct(Product product){
		Errors errors = new BeanPropertyBindingResult(product,Product.class.getName());
		validator.validate(product,errors);
		if(errors.hasErrors()) {
			WebExchangeBindException myException = new WebExchangeBindException(new MethodParameter(null),new BindException(null));
			myException.addAllErrors(errors); 
			throw myException;
		}
		return Mono.just(product);
	}
	
	/**
	 * It catches all errors of the current stream and creates a REST http response with bad request code
	 * @param throwa is the exception of the stream with the error message
	 * @return server response with bad request code and error message
	 */
	private Mono<ServerResponse> responseErrors(Throwable throwa){
		return Mono.just(throwa).cast(WebExchangeBindException.class)
				.flatMap(errors -> Mono.just(errors.getFieldErrors()))
				.flatMapMany(errors -> Flux.fromIterable(errors))//flatmapmany get Mono returns Flux and takes away Flux wrapper
				.map(fieldError -> "Error on Field: "+fieldError.getField()+", Message: "+fieldError.getDefaultMessage())
				.collectList()//it puts all flux string in a single Mono list string
				.flatMap(msglist -> ServerResponse.badRequest().body(fromValue(msglist)));
	}
	
	/**
	 * It returns the list of products as REST json response
	 * It gets the list from mongodb
	 * @return json list of products
	 */
	public Mono<ServerResponse> list(ServerRequest request){
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(myService.findAll(),Product.class);//put Mono inside body, bad practice
	}
	
	/**
	 * It returns the list of categories as REST json response
	 * It gets the list from mongodb
	 * @return json list of categories
	 */
	public Mono<ServerResponse> categories(ServerRequest request){
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(myService.findAllCategory(),Category.class);//put Mono inside body, bad practice
	}
	
	/**
	 * It returns a product as REST json response
	 * The product has id equals to id parameter in REST request
	 * It gets the product from mongodb
	 * @return json product
	 */
	public Mono<ServerResponse> details(ServerRequest request){
		String id = request.pathVariable(paramProductId);
		//flatMap
		return myService.findById(id)
				.flatMap( product -> 
					ServerResponse.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fromValue(product)))//set Mono product as body			
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	/**
	 * It adds the product in the REST request to the mongodb
	 * It generates the creation date of the product
	 * It returns a product as REST json response
	 * @return json product
	 */
	public Mono<ServerResponse> create(ServerRequest request){
		Mono<Product> monoProduct = request.bodyToMono(Product.class);
		//flatmap takes away Mono wrapper from return value, if receive Mono product parameter, it changes to Product
		return monoProduct.flatMap(this::validateProduct).flatMap(productReceived -> {
			Product productCreated = new Product(productReceived);
			productCreated.setCreatedAt(new Date());
			return myService.save(productCreated).flatMap( product2 -> 
				ServerResponse.created(URI.create(urlProducts+product2.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(product2)))			
			.switchIfEmpty(ServerResponse.notFound().build());				
		}).onErrorResume(t->responseErrors(t));
			
		//.switchIfEmpty(ServerResponse.badRequest().build());				
	}
	/**
	 * Updates product with id
	 * It gets the product inside the REST request body, searches in mongodb for a second product with
	 * id equals to the id inside the REST request parameters and updates the second product with the
	 * first product's data
	 * returns the product updated
	 * @return json product
	 */
	public Mono<ServerResponse> edit(ServerRequest request){
		Mono<Product> monoProductChanges = request.bodyToMono(Product.class);//changes to update
		String id = request.pathVariable(paramProductId);
		Mono<Product> monoProductDB = myService.findById(id);//already in DB
		return monoProductDB.zipWith(monoProductChanges,(product1,product2)->{//1=db 2=changes
			product1.setName(product2.getName());
			product1.setPrice(product2.getPrice());
			product1.setCategory(product2.getCategory());
			return product1;
		}).flatMap(this::validateProduct)
				.map(myService::save) //receive Mono Product changes it to Product and returns Mono Product
				.flatMap( monoProductRes -> //receives mono<product> returns mono<ServerResponse>
						ServerResponse.created(URI.create(urlProducts+id))
						.contentType(MediaType.APPLICATION_JSON)
						.body(monoProductRes, Product.class))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(t->responseErrors(t));
		//.switchIfEmpty(ServerResponse.badRequest().build());				
	}
	
	/**
	 * It deletes a product in mongodb with id equals to the id in the REST request
	 * @return no content if found
	 */
	public Mono<ServerResponse> delete(ServerRequest request){
		String id = request.pathVariable(paramProductId);
		Mono<Product> monoProductDB = myService.findById(id);
		return monoProductDB.flatMap( p1-> myService.delete(p1).then(ServerResponse.noContent().build()) )
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	/**
	 * It saves the image inside the REST request and assigns it to a product with id equals to id
	 * in REST request parameters
	 * It saves the image in a server folder
	 * @return json product
	 */
	public Mono<ServerResponse> upload(ServerRequest request){
		String id = request.pathVariable(paramProductId);		
		return request.multipartData().map(multipart->multipart.toSingleValueMap().get(paramProductFileName))
				.cast(FilePart.class)
				.zipWith(myService.findById(id),(file,product)->{
					product.setPicture(Utils.generateUniqueName(file.filename()));
					return file.transferTo(new File(picturesUploadsPath, product.getPicture()))
							.then(myService.save(product));							
				}).flatMap(p->p)//mono<product> -> product
				.flatMap( product2 -> 
					ServerResponse.created(URI.create(urlProducts+product2.getId()))
					.contentType(MediaType.APPLICATION_JSON)
					.body(fromValue(product2)))			
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	/**
	 * It saves the image inside the REST request, saves the product in mogodb with the data inside REST request 
	 * and assigns the image to the product
	 * It saves the image in a server folder
	 * @return json product
	 */
	public Mono<ServerResponse> createWithPicture(ServerRequest request){
		Mono<Product> monoProductReq = request.multipartData().map(multipart->{
			Map<String, Part> singleMap = multipart.toSingleValueMap();
			FormFieldPart name = (FormFieldPart) singleMap.get(paramProductName);
			FormFieldPart price = (FormFieldPart) singleMap.get(paramProductPrice);
			FormFieldPart categoryId = (FormFieldPart) singleMap.get(paramProductCategoryId);
			FormFieldPart categoryName = (FormFieldPart) singleMap.get(paramProductCategoryName);
			Category category = new Category(categoryName.value());
			category.setId(categoryId.value());
			Product product = new Product(name.value(), Double.parseDouble(price.value()), category);
			return product;
		});		
		return request.multipartData().map(multipart->multipart.toSingleValueMap().get(paramProductFileName))
				.cast(FilePart.class)
				.zipWith(monoProductReq.flatMap(this::validateProduct),(file,product)->{
					product.setPicture(Utils.generateUniqueName(file.filename()));
					product.setCreatedAt(new Date());
					return file.transferTo(new File(picturesUploadsPath, product.getPicture()))
							.then(myService.save(product));							
				}).flatMap(p->p)//mono<product> -> product
				.flatMap( product2 -> 
					ServerResponse.created(URI.create(urlProducts+product2.getId()))
					.contentType(MediaType.APPLICATION_JSON)
					.body(fromValue(product2)))
				.onErrorResume(t->responseErrors(t));
	}
}
