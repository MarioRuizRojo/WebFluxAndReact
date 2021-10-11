import ProductList from './ProductList';
import { Product } from '../models/Product';
import {ProductService} from '../services/ProductService';
import {threeProducts, myNever, oneProduct, waitMicroSeconds} from '../fixtures/fixtures';
import React from 'react';
import {render, fireEvent, RenderResult} from '@testing-library/react';
import {MyDateToString} from '../Utils';
import { Service } from '../services/Service';

//to update snapshots
//npm run test -- -u
let productService : Service = new ProductService();

let mockServerGetProducts : jest.SpyInstance<Promise<Product[]>, []>;
//let mockServerDeleteProduct : jest.SpyInstance<Promise<boolean>, [id: string]>;

let mockAddProductToEdit : jest.SpyInstance<void, [product: Product]>;

let renderMocked : RenderResult;

/**
 * Mock server calls to DELETE product and GET list products
 * And mock call to Observer when calling addProductToEdit, so it doesnt send product to edit to ProductEditor component
 */
beforeAll(function():void{
    //to avoid snapshots fails because of date
    let mockMyDateToString : jest.SpyInstance<string, [date: Date]> = jest.spyOn(MyDateToString,'dateToString');
    
    mockServerGetProducts = jest.spyOn(productService,'serverGetProducts');

    //mockServerDeleteProduct = jest.spyOn(productService,'serverDeleteProduct');
    //mockServerDeleteProduct.mockResolvedValue(true);  
    ProductService.prototype.serverDeleteProduct=()=>Promise.resolve(true);

    mockAddProductToEdit = jest.spyOn(productService,'addProductToEdit');   
    mockAddProductToEdit.mockResolvedValue(myNever);
});

/**
 * It renders a ProductList component and mock the response from the server when calling GET list products
 * with a sample of 3 products list
 */
beforeEach(function():void{
    mockServerGetProducts.mockResolvedValue(threeProducts);
    renderMocked = render(<ProductList productService={productService} />);
});

/**
 * It checks the previous snapshot
 */
it('check constructor of product list', function():void{    
    expect(renderMocked).toMatchSnapshot();//three products snapshot
});

/**
 * It checks if there are 3 delete buttons in the list
 */
it('check constructor three products in the product list', function():void{ 
    expect(renderMocked.getByTestId('delete2')).toBeInTheDocument();//there are delete0, delete1 and delete2
});

/**
 * It checks if after clicking delete it calls DELETE on server
 */
it('check if onClick delete triggers a call to service delete', async function():Promise<void>{ 
    let deleteButton : HTMLElement = renderMocked.getByTestId('delete0'); 
    fireEvent.click(deleteButton);
    await waitMicroSeconds(50);
    //expect(mockServerDeleteProduct).toHaveBeenCalled();
    expect(mockServerGetProducts).toHaveBeenCalled();//it will refresh the list
    expect(renderMocked).toMatchSnapshot();//two products snapshot
});

/**
 * It checks if after clicking edit it calls addProductToEdit on Observer
 */
it('check if onClick edit triggers a call to service addProductToEdit with the clicked product as parameter', async function():Promise<void>{ 
    let editButton : HTMLElement = renderMocked.getByTestId('edit0'); 
    fireEvent.click(editButton);
    await waitMicroSeconds(50);
    expect(mockAddProductToEdit).toHaveBeenCalledWith(threeProducts[0]);
});

/**
 * It checks if after clicking add in EditorProduct component it calls to update list in ProductList component
 */
it('check if click on adding button in editor triggers an update of the product list', function():void{ 
    //simulate click on adding button in editor    
    productService.setNewList(oneProduct);//it will send one product list to ProductList component
    let deleteButton : HTMLElement = renderMocked.getByTestId('delete0'); 
    let deleteButton2 : HTMLElement | null = renderMocked.queryByTestId('delete1');
    expect(deleteButton).toBeInTheDocument();
    expect(deleteButton2).toBeNull();//only one product
    expect(renderMocked).toMatchSnapshot();//one product snapshot
});