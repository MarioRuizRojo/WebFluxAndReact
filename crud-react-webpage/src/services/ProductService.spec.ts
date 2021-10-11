import { Product } from "../models/Product";
import { ProductService } from "./ProductService";

let productService : ProductService;
//tells typescript to trust this line, so it is posible to set undefined for testing
let product : Product = undefined!;
let id : string = undefined!;

/**
 * Creates a service to test
 */
beforeAll(function():void{
    productService = new ProductService();
});

/**
 * It expects serverAddProduct call to throw an error because product to add is mandatory parameter but undefined 
 */
it('void product when serverAddProduct',function():void{
    expect(productService.serverAddProduct(product)).rejects.toMatch('product to save cannot be empty');
});

/**
 * It expects serverDeleteProduct call to throw an error because product id to delete is mandatory parameter but undefined 
 */
it('void product when serverDeleteProduct',function():void{
    expect(productService.serverDeleteProduct(id)).rejects.toMatch('id of product to delete cannot be empty');
});

/**
 * It expects serverUpdateProduct call to throw an error because product to update is mandatory parameter but undefined 
 */
it('void product when serverUpdateProduct',function():void{
    expect(productService.serverUpdateProduct(product)).rejects.toMatch('product to update cannot be empty');
});