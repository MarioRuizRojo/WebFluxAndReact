import { Observable, Subject } from 'rxjs';

import { urlRESTAPI } from '../constants';
import { Category } from '../models/Category';
import { Product } from '../models/Product';

/**
 * Service to make request calls to the server and implement observer pattern
 * to communicate components with each other
 */
export class ProductService {
    /**
     * observer pattern
     * to communicate ProductEditor with ProductList
     * when click in edit button
     */
    private productToEdit$ : Subject<Product> = new Subject<Product>();
    /**
     * observer pattern
     * to communicate ProductEditor with ProductList
     * when click in form's submit button of the ProductEditor
     */
    private productList$ : Subject<Product[]> = new Subject<Product[]>();

    //----------SERVER FUNCTIONS----------
    /**
     * Request to the server to get the category list
     * @returns promise of the request process with a category list if it succeeds
     */
     public serverGetCategories(): Promise<Category[]> {
        return new Promise(function (resolve, reject): void {
            fetch(urlRESTAPI+'/categories')
            .then(function (answer): Promise<any> {
                return answer.json();
            })
            .then(function (json: any): void {
                try {
                    let categoryList1: Category[] = [];
                    let list: any[] = json;
                    list.forEach(function (categoryJson: any): void {
                        let category: Category = new Category(categoryJson);
                        categoryList1.push(category);
                    });
                    resolve(categoryList1);
                } catch (error1) {
                    reject(error1);
                }
            })
            .catch((error2: Error) => {
                console.error(error2.message);
                reject(error2);
            });
        });
     }
     
    /**
     * Request to the server to add a product to the list
     * @param productToSave product to add
     * @returns promise of the request process
     */
    public serverAddProduct(productToSave: Product): Promise<any> {        
        let promiseJSON: Promise<any>;
        if (productToSave == undefined) {
            promiseJSON = Promise.reject("product to save cannot be empty");
        } else {
            promiseJSON = fetch(
                urlRESTAPI,
                {
                    method: 'POST',
                    body: JSON.stringify(productToSave),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            )
            .then(function (answer): Promise<any> {
                return answer.json();
            });
        }
        return promiseJSON;
    }

    /**
     * Request to the server to get the product list
     * @returns promise of the request process with a product list if it succeeds
     */
    public serverGetProducts(): Promise<Product[]> {
        return new Promise(function (resolve, reject): void {
            fetch(urlRESTAPI)
            .then(function (answer): Promise<any> {
                return answer.json();
            })
            .then(function (json: any): void {
                try {
                    let productList1: Product[] = [];
                    let list: any[] = json;
                    list.forEach(function (productJson: any): void {
                        let product: Product = new Product(productJson);
                        productList1.push(product);
                    });
                    resolve(productList1);
                } catch (error1) {
                    reject(error1);
                }
            })
            .catch((error2: Error) => {
                console.error(error2.message);
                reject(error2);
            });
        });
    }

    /**
     * Request to the server to delete a product that is in the list
     * @param id identifier of the productto delete
     * @returns promise of the request process
     */
    public serverDeleteProduct(id: string): Promise<boolean> {
        let promiseJSON: Promise<boolean>;
        if (id == undefined) {
            promiseJSON = Promise.reject("id of product to delete cannot be empty");
        } else {
            promiseJSON = new Promise(
                function(resolve: (value: boolean | PromiseLike<boolean>) => void, reject: (reason?: any)=>void) : void{
                    fetch(
                        urlRESTAPI+'/'+id,
                        {
                            method: 'DELETE'
                        }
                    )
                    .then(function (answer:Response): void {
                        resolve(answer.ok);
                    })
                    .catch(function(error:Error):void{
                        reject(error);
                    });
                }
            );
        }
        return promiseJSON;
    }

    /**
     * Request to the server to update a product that is in the list
     * @param productToUpdate identifier and product new data to update
     * @returns promise of the request process
     */
    public serverUpdateProduct(productToUpdate: Product): Promise<any> {
        let promiseJSON: Promise<any>;
        if (productToUpdate == undefined) {
            promiseJSON = Promise.reject('product to update cannot be empty');
        }
        else {
            promiseJSON = fetch(
                urlRESTAPI+'/'+productToUpdate.getId(),
                {
                    method: 'PUT',
                    body: JSON.stringify(productToUpdate),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            )
            .then(function (answer:Response): Promise<any> {
                return answer.json();
            });
        }
        return promiseJSON;
    }
    //-------RX observer pattern--------
    //----------RXjs FUNCTIONS----------
    //EDIT PRODUCT
    /**
     * It is called from ProductList
     * 
     * ProductEditor is subscribed to its updates
     * 
     * When it updates ProductEditor
     * enter edit mode and fill form with the data of the product 
     * to edit
     * @param product is product to edit
     */
    public addProductToEdit(product: Product) : void {
        this.productToEdit$.next(product);
    }

    /**
     * ProductEditor uses it to subscribe to updates on productToEdit$
     */
    public getProductToEdit(): Observable<Product> {
        return this.productToEdit$.asObservable();
    }
    //UPDATE LIST
    /**
     * It is called from ProductEditor when submit button is clicked
     * 
     * ProductList is subscribed to its updates
     * 
     * When it updates ProductList renders again the product list
     * with the new product list data
     * @param newProductList new product list to render
     */
    public setNewList(newProductList: Product[]) : void{
        this.productList$.next(newProductList);
    }

    /**
     * ProductList uses it to subscribe to updates on productList$
     */
    public getNewList(): Observable<Product[]> {
        return this.productList$.asObservable();
    }
}


