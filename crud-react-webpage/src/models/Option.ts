/**
 * Default class override to manage the info inside react-select component
 */
export class Option{

    /**
     * Identifier of the option to distint from others in the selector
     */
    public value : string;
    /**
     * label of the option to show in the selector
     */
    public label : string;

    /**
     * Constructor from jsonObject
     * @param option jsonObject
     */
    constructor(option:any){
        if(option==undefined){
            this.value = '0';
            this.label = '';
        }
        else{
            this.value = option.value;
            this.label = option.label;
        }        
    }

    /**
     * 
     * @returns identifier of the option
     */
    public getValue():string{
        return this.value;
    }
    /**
     * 
     * @returns caption of the option to show
     */
    public getLabel():string{
        return this.label;
    }
}

export let optionDefault : Option = new Option({value:'0',label:''});