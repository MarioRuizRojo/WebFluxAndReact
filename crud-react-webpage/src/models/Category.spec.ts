import { Category } from "./Category";

let category: Category;

/**
 * It creates a category as test data
 */
beforeEach(function():void{
    category = new Category({id:'id1',name:'name1'});
});

/**
 * It expects to not throw error if constructor parameter is undefined
 */
it('constructor doesnt crash if parameter is undefined',function():void{
    let creation = function():void{
        category = new Category(undefined);
    }
    expect(creation).not.toThrow(Error);
});

/**
 * It expects to get the same id setted in constructor
 */
it('getId what it setted in constructor before',function():void{    
    expect(category.getId()).toBe('id1');
});

/**
 * It expects to get the same name setted in constructor
 */
it('getName what it setted in constructor before',function():void{    
    expect(category.getName()).toBe('name1');
});

/**
 * It expects to get the same id setted just before
 */
it('getId what it setted with set before',function():void{ 
    category.setId('id2');
    expect(category.getId()).toBe('id2');
});

/**
 * It expects to get the same name setted just before
 */
it('getName what it setted with set before',function():void{ 
    category.setName('name2');
    expect(category.getName()).toBe('name2');
});