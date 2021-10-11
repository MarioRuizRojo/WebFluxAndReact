import { Observable, Subject } from 'rxjs';

import { Category } from '../models/Category';
import { Product } from '../models/Product';
import { Service } from './Service';

/**
 * Debug service, mocked
 */
export class ProductServiceDebug implements Service{
    private products : Product[] = [];
    private categories : Category[] = [];

    private productToEdit$ : Subject<Product> = new Subject<Product>();
    
    private productList$ : Subject<Product[]> = new Subject<Product[]>();

    constructor(){
        let category1 : Category = new Category({id:'0',name:'category1'});
        let category2 : Category = new Category({id:'1',name:'category2'});
        this.categories.push(category1);
        this.categories.push(category2);
        let product1 : Product = new Product({name:'name1',price:0.01,category:category1});
        let product2 : Product = new Product({name:'name2',price:4.31,category:category1});
        let product3 : Product = new Product({name:'name3',price:354.08,category:category2});
        product1.setId('01');
        product2.setId('02');
        product3.setId('03');
        this.products.push(product1);
        this.products.push(product2);
        this.products.push(product3);
    }

    //----------SERVER FUNCTIONS----------

     public serverGetCategories(): Promise<Category[]> {
        let myThis : this = this;
        return new Promise(function (resolve, reject): void {
            resolve(myThis.categories);
        });
     }
    
    public serverAddProduct(productToSave: Product): Promise<any> {
        productToSave.setId(this.products.length.toString());
        console.log('ey');
        console.log(productToSave);
        let myThis : this = this;
        return new Promise(function (resolve, reject): void {
            myThis.products.push(productToSave);
            resolve(productToSave);
        });
    }

    public serverGetProducts(): Promise<Product[]> {
        let myThis : this = this;
        return new Promise(function (resolve, reject): void {
            resolve(myThis.products);
        });
    }

    public serverDeleteProduct(id: string): Promise<boolean> {
        let prodAux : Product = new Product(undefined);
        prodAux.setId(id);
        let myThis : this = this;
        return new Promise(function (resolve, reject): void {            
            let index : number = myThis.myFind(prodAux,myThis.products);
            if(index==-1)
                reject('that product doesnt exist');
            else{
                myThis.products.splice(index,1);
                resolve(true);
            }                
        });
    }

    private myFind(productToFind:Product, aProductList:Product[]):number{
        let i : number = 0;
        while(i<aProductList.length){
            let productAux : Product = aProductList[i];
            if(productToFind.getId()==productAux.getId()){
                return i;
            }
            i++;
        }
        return -1;
    }
    public serverUpdateProduct(productToUpdate: Product): Promise<any> {
        let myThis : this = this;
        return new Promise(function (resolve, reject): void {            
            let index : number = myThis.myFind(productToUpdate,myThis.products);
            if(index==-1)
                reject('that product doesnt exist');
            else{
                console.log(index);
                myThis.products[index]=productToUpdate;
                resolve(true);
            }                
        });
    }
    //-------RX observer pattern--------
    //----------RXjs FUNCTIONS----------
    public addProductToEdit(product: Product) {
        this.productToEdit$.next(product);
    }

    public getProductToEdit(): Observable<Product> {
        return this.productToEdit$.asObservable();
    }
    public setNewList(newProductList: Product[]) {
        this.productList$.next(newProductList);
    }

    public getNewList(): Observable<Product[]> {
        return this.productList$.asObservable();
    }
}


