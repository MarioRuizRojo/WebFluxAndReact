import { Observable, Subject } from 'rxjs';

import { urlRESTAPI } from '../constants';
import { Category } from '../models/Category';
import { Product } from '../models/Product';

/**
 * Service generic
 */
export interface Service {    

    //----------SERVER FUNCTIONS----------
    /**
     * Request to the server to get the category list
     * @returns promise of the request process with a category list if it succeeds
     */
    serverGetCategories(): Promise<Category[]>;    
    /**
     * Request to the server to add a product to the list
     * @param productToSave product to add
     * @returns promise of the request process
     */
    serverAddProduct(productToSave: Product): Promise<any>;
    /**
     * Request to the server to get the product list
     * @returns promise of the request process with a product list if it succeeds
     */
    serverGetProducts(): Promise<Product[]>;
    /**
     * Request to the server to delete a product that is in the list
     * @param id identifier of the productto delete
     * @returns promise of the request process
     */
    serverDeleteProduct(id: string): Promise<boolean>;
    /**
     * Request to the server to update a product that is in the list
     * @param productToUpdate identifier and product new data to update
     * @returns promise of the request process
     */
    serverUpdateProduct(productToUpdate: Product): Promise<any>;
    //-------RX observer pattern--------
    //----------RXjs FUNCTIONS----------
    /**
     * It is called from ProductList to send product to ProductEditor 
     * @param product is product to edit
     */
    addProductToEdit(product: Product):void;
    /**
     * It makes ProductEditor to be able to receive productToEdit from ProductList
     */
    getProductToEdit(): Observable<Product>;
    /**
     * It is called from ProductEditor to send new product list data to ProductList
     * @param newProductList new product list to render
     */
    setNewList(newProductList: Product[]):void;
    /**
     * It makes ProductList to be able to receive newProductList from ProductEditor
     */
    getNewList(): Observable<Product[]>;
}


