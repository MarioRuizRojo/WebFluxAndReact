import { Option } from "./Option";

let option: Option;

/**
 * It creates an option as test data
 */
beforeEach(function():void{
    option = new Option({value:'value1',label:'label1'});
});

/**
 * It expects to not throw error if constructor parameter is undefined
 */
it('constructor doesnt crash if parameter is undefined',function():void{
    let creation = function():void{
        option = new Option(undefined);
    }
    expect(creation).not.toThrow(Error);
});

/**
 * It expects to get the same value setted in constructor
 */
it('getValue what it setted in constructor before',function():void{    
    expect(option.getValue()).toBe('value1');
});

/**
 * It expects to get the same label setted in constructor
 */
it('getLabel what it setted in constructor before',function():void{    
    expect(option.getLabel()).toBe('label1');
});

/**
 * It expects to get the same value setted just before
 */
it('getValue what it setted with set before',function():void{ 
    option.value = 'value2';
    expect(option.getValue()).toBe('value2');
});

/**
 * It expects to get the same name setted just before
 */
it('getLabel what it setted with set before',function():void{ 
    option.label = 'name2';
    expect(option.getLabel()).toBe('name2');
});