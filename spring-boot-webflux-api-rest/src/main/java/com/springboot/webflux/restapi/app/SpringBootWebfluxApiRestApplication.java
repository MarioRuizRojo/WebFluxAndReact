package com.springboot.webflux.restapi.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.springboot.webflux.restapi.app.models.dao.CategoryDao;
import com.springboot.webflux.restapi.app.models.dao.ProductDao;
import com.springboot.webflux.restapi.app.models.documents.Category;
import com.springboot.webflux.restapi.app.models.documents.Product;

import reactor.core.publisher.Flux;

/**
 * 
 * @author Mario Ruiz Rojo
 * 
 * Main Spring App to setup the REST api and run mongodb script
 *
 */
@SpringBootApplication
public class SpringBootWebfluxApiRestApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApiRestApplication.class);
	
	@Autowired
	private ProductDao serviceProductMongo;
	@Autowired
	private CategoryDao serviceCategoryMongo;
	@Autowired
	private ReactiveMongoTemplate reacMongoTempl;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApiRestApplication.class, args);
	}

	/**
	 * MongoDB script
	 * 	creates list of 2 categories
	 *  creates list of 9 products
	 */
	@Override
	public void run(String... args) throws Exception {
		reacMongoTempl.dropCollection("products").subscribe();
		reacMongoTempl.dropCollection("categories").subscribe();
		Category electronics = new Category("electronics");
		Category sports = new Category("sports");
		Category furniture = new Category("furniture");
		Category computers = new Category("computers");
		Flux.just(electronics,sports,furniture,computers)
		.flatMap(serviceCategoryMongo::save)
		.doOnNext(category->log.info("==>[C]"+category.getId()+" "+category.getName()))
		.thenMany(		
			Flux.just(
					new Product("TV Panasonic Screen LCD", 456.89, electronics),
					new Product("Sony Camera HD Digital", 177.89, electronics),
					new Product("Apple iPod", 46.89, electronics),
					new Product("Sony Notebook", 846.89, computers),
					new Product("Hewlett Packard Multifuncional", 200.89, computers),
					new Product("Bianchi bicycle", 70.89, sports),
					new Product("HP Notebook Omen 17", 2500.89, computers),
					new Product("Rodulf Ikea Desk Table", 150.89,furniture),
					new Product("TV Sony Bravia OLED 4K Ultra HD", 2255.89, electronics)
					)
			.flatMap(product->{//flatmap takes away Mono wrapper from save result
				product.setCreatedAt(new Date());
				return serviceProductMongo.save(product);
			})
		)
		.subscribe(prod->log.info("==>[P]"+prod.getId()+" "+prod.getName()));
	}

}
