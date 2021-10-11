/**
 * It represents category data
 */
export class Category{
    /**
     * Identifier of category
     */
    private id : string;
    /**
     * Name of category
     */
    private name : string;

    /**
     * Constructor from jsonObject
     * @param category jsonObject
     */
    constructor(category : any){
        if(category==undefined){
            this.id='';
            this.name='';
        }
        else{
            this.id = category.id;
            this.name = category.name;
        }        
    }

    /**
     * 
     * @returns identifier of category
     */
    public getId(): string {
        return this.id;
    }
    /**
     * 
     * @param value to set identifier of category
     */
    public setId(value: string) {
        this.id = value;
    }

    /**
     * 
     * @returns name of category
     */
    public getName(): string {
        return this.name;
    }
    /**
     * 
     * @param value to set name of category
     */
    public setName(value: string) {
        this.name = value;
    }
}
//export {Category}