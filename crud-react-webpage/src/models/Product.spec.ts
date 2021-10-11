import { Category } from "./Category";
import { Product } from "./Product";

let product: Product;
let category1 : Category = new Category({id:'id1',name:'name1'});

/**
 * It creates a product with most used attributes
 */
beforeEach(function():void{    
    product = new Product({name: 'name1', price: 1, category: category1});
});


/**
 * It expects to not throw error if constructor parameter is undefined
 */
it('constructor doesnt crash if parameter is undefined',function():void{
    let creation = function():void{
        product = new Product(undefined);
    }
    expect(creation).not.toThrow(Error);
});


/**
 * It expects to get the same name setted in constructor
 */
it('getName what it setted in constructor before',function():void{    
    expect(product.getName()).toBe('name1');
});

/**
 * It expects to get the same price setted in constructor
 */
it('getPrice what it setted in constructor before',function():void{    
    expect(product.getPrice()).toBe(1);
});

/**
 * It expects to get the same category setted in constructor
 */
it('getCategory what it setted in constructor before',function():void{ 
    expect(product.getCategory()).toStrictEqual(category1);
});


/**
 * It expects to get the same id setted just before
 */
it('getId what it setted with set before',function():void{ 
    product.setId('id1');
    expect(product.getId()).toBe('id1');
});

/**
 * It expects to get the same name setted just before
 */
it('getName what it setted with set before',function():void{ 
    product.setName('name2');
    expect(product.getName()).toBe('name2');
});

/**
 * It expects to get the same price setted just before
 */
it('getPrice what it setted with set before',function():void{ 
    product.setPrice(2);
    expect(product.getPrice()).toBe(2);
});

/**
 * It expects to get the same date as CreatedAt setted just before
 */
it('getCreatedAt what it setted with set before',function():void{
    let now : Date = new Date();
    product.setCreatedAt(now);
    expect(product.getCreatedAt()).toBe(now);
});

/**
 * It expects to get the same category setted just before
 */
it('getCategory what it setted with set before',function():void{
    let category2 : Category = new Category({id:'id2',name:'name2'}); 
    product.setCategory(category2);
    expect(product.getCategory()).toBe(category2);
});

/**
 * It expects to get the same picture setted just before
 */
it('getPicture what it setted with set before',function():void{
    product.setPicture('picture1');
    expect(product.getPicture()).toBe('picture1');
});