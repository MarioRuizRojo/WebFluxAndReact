import App from '../App';
import {ProductService} from '../services/ProductService';
import {fireEvent, render, RenderResult} from '@testing-library/react';
import React from 'react';
import { threeCategories, twoProducts, waitMicroSeconds } from '../fixtures/fixtures';
import { Product } from '../models/Product';
import { Category } from '../models/Category';

let mockServerGetProducts : jest.SpyInstance<Promise<Product[]>, []>;
let mockServerGetCategories : jest.SpyInstance<Promise<Category[]>, []>;
let mockServerUpdateProduct : jest.SpyInstance<Promise<any>, [productToUpdate: Product]>;
let mockServerAddProduct : jest.SpyInstance<Promise<any>, [productToSave: Product]>;

let renderMocked:RenderResult;

/**
 * Mock all server calls:
 * DELETE product, GET list products, POST create product, PUT update product and GET list categories * 
 */
beforeAll(function():void{
    mockServerGetProducts = jest.spyOn(ProductService.prototype,'serverGetProducts');
    mockServerGetCategories = jest.spyOn(ProductService.prototype,'serverGetCategories');
    mockServerUpdateProduct = jest.spyOn(ProductService.prototype,'serverUpdateProduct');
    mockServerAddProduct = jest.spyOn(ProductService.prototype,'serverAddProduct');

    mockServerUpdateProduct.mockResolvedValue({});
    mockServerAddProduct.mockResolvedValue({});

    //ProductService.prototype.serverGetProducts=()=>Promise.resolve([]);
    //ProductService.prototype.serverUpdateProduct=()=>Promise.resolve({});
    ProductService.prototype.serverDeleteProduct=()=>Promise.resolve(true);    
});

/**
 * Mock GET list products to return empty list and GET list categories to return 3 categories list
 */
beforeEach(function():void{
    mockServerGetCategories.mockResolvedValue(threeCategories);
    mockServerGetProducts.mockResolvedValue([]);    
    renderMocked = render(<App />);
});

/**
 * It adds 2 products
 * So expects 2 products in the list
 * expects button to be disabled after submit
 * It edit 2nd product, types 'w' adding it to product's name
 * So expects 2nd product to have 'name02w' as name
 * deletes 1st product so checks only remaining product to have name equals to 'name02w'
 */
it('Integration test 1',async function():Promise<void>{
    let inputName : any =  renderMocked.getByTestId('idName');
    let inputPrice : any =  renderMocked.getByTestId('idPrice');
    let submitButton : HTMLElement =  renderMocked.getByTestId('idSubmitButton');
    let body : HTMLElement =  renderMocked.getByTestId('idTableProducts');
    expect(body.childElementCount).toBe(0);
    expect(inputName).toBeInTheDocument();
    expect(inputPrice).toBeInTheDocument();
    expect(submitButton).toBeInTheDocument();
    //add 2 products
    fireEvent.change(inputName,{target:{value:twoProducts[0].getName()}});
    fireEvent.change(inputPrice,{target:{value:twoProducts[0].getPrice()}});    
    fireEvent.click(submitButton);
    await waitMicroSeconds(50);
    fireEvent.change(inputName,{target:{value:twoProducts[1].getName()}});
    fireEvent.change(inputPrice,{target:{value:twoProducts[0].getPrice()}});
    fireEvent.click(submitButton);
    mockServerGetProducts.mockResolvedValue(twoProducts); 
    await waitMicroSeconds(50);
    //button is disabled after submit
    expect(submitButton).toBeDisabled();
    //there are 2 products in the list
    expect(body.childElementCount).toBe(2);
    //edit 2nd product
    let editButton : HTMLElement =  renderMocked.getByTestId('edit1');
    fireEvent.click(editButton);
    await waitMicroSeconds(50);
    //types 'w' in product's name
    fireEvent.change(inputName,{target:{value:twoProducts[1].getName()+'w'}});
    let secondProdEdit : Product = new Product(twoProducts[1]);
    secondProdEdit.setName('name02w');
    mockServerGetProducts.mockResolvedValue([twoProducts[0],secondProdEdit]); 
    fireEvent.click(submitButton);
    await waitMicroSeconds(50);    
    //2nd product's name is 'name02w'
    let original : string = body.innerHTML;
    let replaced : string = original.replace('name02w','');
    //expects body.innerHTML to contains 'name02w'
    expect(replaced.length).toBeLessThan(original.length);
    //deletes 1st product
    let deleteButton : HTMLElement =  renderMocked.getByTestId('delete0');
    mockServerGetProducts.mockResolvedValue([secondProdEdit]); 
    fireEvent.click(deleteButton);
    await waitMicroSeconds(50);
    //let tdName : any = body.firstChild?.firstChild?.nextSibling;
    let txt : string | null | undefined = body.firstChild?.firstChild?.nextSibling?.textContent;
    expect(txt).toBe('name02w');
});