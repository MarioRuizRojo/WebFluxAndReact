import {Category} from './Category'

/**
 * It represents product data
 */
export class Product{
    /**
     * Identifier of product 
     */
    private id: string;   
    /**
     * Name of product 
     */ 
    private name: string; 
    /**
     * Price of product 
     */    
    private price: number;  
    /**
     * Creation date of product 
     */   
    private createdAt: Date;   
    /**
     * Category of product 
     */  
    private category: Category;  
    /**
     * Picture file name of product 
     */   
    private picture: string;    

    /**
     * Constructor from jsonObject
     * @param product jsonObject
     */
    constructor(product : any){
        if(product==undefined){
            this.id = '';
            this.name = '';
            this.price = 0.0;
            this.createdAt = new Date();
            this.category = new Category(undefined);
            this.picture = '';
        }else{
            this.id = product.id;
            this.name = product.name;
            try{
                this.price = parseFloat(product.price);
            }
            catch(e){
                this.price = 0.0
            }
            this.createdAt = new Date(product.createdAt);
            this.category = new Category(product.category);
            this.picture = product.picture;
        }        
    }

    /**
     * 
     * @returns Identifier
     */
    public getId(): string {
        return this.id;
    }
    /**
     * 
     * @param value to set Identifier of product
     */
    public setId(value: string) {
        this.id = value;
    }
    /**
     * 
     * @returns Name of product
     */
    public getName(): string {
        return this.name;
    }
    /**
     * 
     * @param value to set Name of product
     */
    public setName(value: string) {
        this.name = value;
    }
    /**
     * 
     * @returns Price of product
     */
    public getPrice(): number {
        return this.price;
    }
    /**
     * 
     * @param value to set Price of product
     */
    public setPrice(value: number) {
        this.price = value;
    }
    /**
     * 
     * @returns Creation date of product
     */
    public getCreatedAt(): Date {
        return this.createdAt;
    }
    /**
     * 
     * @param value to set Creation date of product
     */
    public setCreatedAt(value: Date) {
        this.createdAt = value;
    }
    /**
     * 
     * @returns Category
     */
    public getCategory(): Category {
        return this.category;
    }
    /**
     * 
     * @param value to set Category of product
     */
    public setCategory(value: Category) {
        this.category = value;
    }
    /**
     * 
     * @returns Picture file name of product
     */
    public getPicture(): string {
        return this.picture;
    }
    /**
     * 
     * @param value to set Picture file name of product
     */
    public setPicture(value: string) {
        this.picture = value;
    }
}