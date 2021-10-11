import React from 'react';
import {Component} from 'react';
import Select, { ActionMeta, SingleValue } from 'react-select';
import { Option, optionDefault } from '../models/Option';
import { Category } from '../models/Category';
import { Product } from '../models/Product';
import { Service } from '../services/Service';
import { clone } from '../Utils';

type Props = {productService:Service};
type State = {loading : boolean, editing : boolean, categoriesOptions : Option[],
    inputName : string, inputPrice : number, categoryOptionSelected : Option, formValid : boolean};
/**
 * Component to render the formulary of edition of products with add and update buttons
 */
export default class ProductEditor extends Component<Props, State>{
    private categories : Category[];
    private productService : Service;
    private product : Product;

    /**
     * Contructor from Props,set the interface to make calls to server and other components
     * Initial category list empty, initial edition product empty and loading flag true
     * @param props 
     */
    constructor(props:Props){
        super(props);
        this.productService = props.productService;
        this.categories = [];
        this.product = new Product(undefined);
        this.state = {
            loading: true,
            editing: false,
            categoriesOptions : [],            
            inputName : '',
            inputPrice : 0,
            categoryOptionSelected : optionDefault,
            formValid : false
        }
    }
    
    /**
     * It calls to server GET categories and then creates option list to category selector.
     * It resets the formulary. Already loaded so loading flag false.
     * It subscribes to changes on onEditProduct observable
     */
    componentDidMount(){
        this.productService.serverGetCategories().then((categories : Category[]) => {
            this.categories = categories;
            let categoriesOptions : Option[] = [];
            categories.forEach((category: Category)=>{
                let option : Option = new Option({value:category.getId(), label:category.getName()});
                categoriesOptions.push(option);
            });
            //update state
            let state : any = clone(this.state);
            state.categoriesOptions = categoriesOptions;
            state.loading = false;
            this.resetForm(this);
            this.setState(state);
        });
        this.productService.getProductToEdit().subscribe((toEdit:Product)=>this.onEditProduct(toEdit,this));

    }

    /**
     * Load all info of the productToEdit in the formulary, then re-renders the component
     * @param toEdit 
     * @param myThis 
     */
    private onEditProduct(toEdit:Product, myThis : this):void{
        let state : any = clone(myThis.state);
        let product : Product = myThis.product;
        myThis.resetForm(myThis);
        product.setId(toEdit.getId());
        product.setName(toEdit.getName());
        product.setCreatedAt(toEdit.getCreatedAt());
        product.setPrice(toEdit.getPrice());
        product.setCategory(toEdit.getCategory());
        product.setPicture(toEdit.getPicture());
        state.inputPrice=toEdit.getPrice();
        state.inputName=toEdit.getName();
        state.categoryOptionSelected=myThis.getOptionByValue(toEdit.getCategory().getId(),myThis);
        state.formValid = true;
        state.editing = true;
        state.loading = false;
        this.setState(state);
    }

    /**
     * It reset the formulary and re-renders this component
     * @param myThis 
     */
    private resetForm(myThis:this):void{
        let state : any = clone(myThis.state);
        let product : Product = myThis.product;
        product.setId('');
        product.setName('');
        product.setCreatedAt(new Date());
        product.setPrice(0.0);
        product.setPicture('');
        product.setCategory(myThis.categories[0]);
        state.inputPrice = 0;
        state.inputName = '';
        state.categoryOptionSelected = optionDefault;
        state.formValid = false;
        state.editing = false;
        state.loading = false;
        myThis.setState(state);
    }

    /**
     * It returns the category with identifier equals to id
     * @param id of category
     * @returns category
     */
    private getCategoryById(id:string):Category{
        let category : Category | undefined = this.categories.find((category:Category)=>category.getId()==id);
        if(category!=undefined)
            return category;
        return new Category(undefined);
    }

    /**
     * It returns the option with identifier equals to value
     * @param value is the identifier of option
     * @param myThis 
     * @returns option
     */
    private getOptionByValue(value:string, myThis:this):Option{
        let option : Option | undefined = myThis.state.categoriesOptions.find((opti:Option)=>opti.value==value);
        if(option!=undefined)
            return option;
        return optionDefault;
    }

    /**
     * On select event handler that re-renders the selector to show the user selection
     * @param optionSelected option clicked by the user in the selector
     * @param actionMeta not used
     * @param myThis 
     */
    private onSelected(optionSelected: SingleValue<Option>, actionMeta: ActionMeta<Option>, myThis: this):void{
        let selectedOp : Option = new Option(optionSelected);
        let category : Category = myThis.getCategoryById(selectedOp.getValue());
        let state : any = clone(myThis.state);
        let product : Product = myThis.product;
        if(category!=undefined)
            product.setCategory(category);
        state.categoryOptionSelected = optionSelected;
        myThis.setState(state);
    }

    /**
     * On change event handler that enable or disable the add button if the data in the inputs is valid
     * @param event 
     * @param myThis 
     */
    private onChangeField(event : React.ChangeEvent<HTMLInputElement>, myThis : this):void{
        let state : any = clone(myThis.state);
        let name : string = event.target.name;
        let product : Product = myThis.product;
        let value : string=event.target.value;
        switch(name){
            case 'nameName':
                state.inputName=value;
                product.setName(value);
                break;
            case 'namePrice':
                let price : number = value.length>0?parseFloat(value):0;
                state.inputPrice = price;
                product.setPrice(price);
                break;
            default:
        }        
        state.formValid = (state.inputName.length>0) && (state.inputPrice>0.0);
        myThis.setState(state);
    }

    /**
     * On submit formulary event that calls to POST create on server if user is adding new product and 
     * calls to PUT update on server if user is editing an existing product.
     * It resets the form, calls to GET list products on server and send the list to ProductList component.
     * It prevents the page to refresh
     * @param event 
     * @param myThis 
     */
    private async submit(event :React.FormEvent<HTMLFormElement>, myThis : this):Promise<void>{
        event.preventDefault();//to prevent refresh webpage after submit
        let state = clone(myThis.state);
        let product : Product = new Product(clone(myThis.product));
        let price: number = myThis.state.inputPrice;
        let name: string = myThis.state.inputName;
        let categoryOptSel : Option = new Option(myThis.state.categoryOptionSelected);
        let category: Category = myThis.getCategoryById(categoryOptSel.getValue());
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);      
        let responseJson;
        if (myThis.state.editing) {
            responseJson = await myThis.productService.serverUpdateProduct(product);
        } else {
            responseJson = await myThis.productService.serverAddProduct(product);
        }
        //console.log(responseJson);
        myThis.resetForm(myThis);
        //RX observer pattern
        myThis.productService.setNewList(
            await myThis.productService.serverGetProducts()
        );
    }

    render(){
        return(
            <div className="card">
                <div className="card-body">
                    <h5 className="title mb-3">
                        <span>Product Editor</span>
                        {this.state.loading && 
                            <div className="spinner-border float-end" role="status">
                                <span className="visually-hidden">loading...</span>
                            </div>
                        }
                    </h5>
                    <form onSubmit={e=>this.submit(e,this)}>
                        <div className='form-group'>
                            <label htmlFor='nameName'>Name</label>
                            <input type='text' value={this.state.inputName} className='form-control' id='idName' 
                                data-testid='idName'
                                onChange={e=>this.onChangeField(e,this)} name='nameName' required/>
                        </div>
                        <div className='form-group'>
                            <label htmlFor='namePrice'>Price</label>
                            <input type='number' value={this.state.inputPrice} className='form-control' id='idPrice' 
                                data-testid='idPrice'
                                onChange={e=>this.onChangeField(e,this)} name='namePrice' max='200000' min='0' step='0.001'/>
                        </div>
                        <div className='form-group'>
                            <label htmlFor='nameCategorySelector'>Category</label>
                            <Select options={this.state.categoriesOptions}
                                value={this.state.categoryOptionSelected}
                                className='form-control' id='idCategorySelector' name='nameCategorySelector' 
                                data-testid='idCategorySelector'
                                onChange={(option:SingleValue<Option>,action:ActionMeta<Option>)=>this.onSelected(option,action,this)}/>
                        </div>                    
                        <div className="d-grid gap-2">
                            <button className="btn btn-success btn-lg" type="submit" data-testid='idSubmitButton' id='idSubmitButton'
                                disabled={!this.state.formValid}>
                                    <i className="fas fa-database" />
                                    {this.state.editing ? 'savechanges': 'add'}
                            </button>
                        </div>
                    </form>
                </div>
                </div>
        );
    }
}