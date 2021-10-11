import { Category } from "../models/Category";
import { Product } from "../models/Product";
/**
 * JSON data to create categories
 */
export let jsonCategory01 : any = {id:'id01',name:'name01'};
export let jsonCategory02 : any = {id:'id02',name:'name02'};
export let jsonCategory03 : any = {id:'id03',name:'name03'};
/**
 * 3 sample categories
 */
export let category01 : Category = new Category(jsonCategory01);
export let category02 : Category = new Category(jsonCategory02);
export let category03 : Category = new Category(jsonCategory03);

export let dateFixed : Date = new Date(Date.UTC(1995, 4, 23));
/**
 * JSON data to create products
 */
export let jsonProduct01 : any = {id:'id01',name:'name01',price:1,createdAt:dateFixed,category:category01,picture:'picture01'};
export let jsonProduct02 : any = {id:'id02',name:'name02',price:2.56,createdAt:dateFixed,category:category02,picture:'picture02'};
export let jsonProduct03 : any = {id:'id03',name:'name03',price:3.3,createdAt:dateFixed,category:category01,picture:'picture03'};
/**
 * 3 sample products
 */
export let product01 : Product = new Product(jsonProduct01);
export let product02 : Product = new Product(jsonProduct02);
export let product03 : Product = new Product(jsonProduct03);

/**
 * 2 sample list of categories
 */
export let twoCategories : Category[] = [category01,category02];
export let threeCategories : Category[] = [category01,category02,category03];

/**
 * 3 sample list of products
 */
export let oneProduct : Product[] = [product01];
export let twoProducts : Product[] = [product01,product02];
export let threeProducts : Product[] = [product01,product02,product03];

/**
 * never value for weird cases
 */
export let myNever : never = undefined!;

/**
 * void function for mocking
 */
export function dummyFunction():void{

}

/**
 * Delay in micro seconds for tests
 * @param micros is microseconds to wait
 */
export function waitMicroSeconds(micros:number):Promise<void>{
    return new Promise(function(resolve : (value: void | PromiseLike<void>) => void,reject : (reason?: any) => void):void{
        setTimeout(function():void{
            resolve();
        },micros);
    });
}