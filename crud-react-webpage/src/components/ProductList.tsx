import React from 'react';
import {Component} from 'react';
import {FaPenSquare, FaTrash} from 'react-icons/fa';
import {Product} from '../models/Product';
import { Service } from '../services/Service';
import { fourLastChars, MyDateToString } from '../Utils';

type Props = {productService:Service};
type State = {productList:Product[]};

/**
 * Component to render the list of products with delete and edit buttons related to each product
 */
export default class ProductList extends Component<Props, State>{
    private productService : Service;

    /**
     * Contructor from Props, set the interface to make calls to server and other components
     * Initial product list empty
     * @param props 
     */
    constructor(props:Props){
        super(props);
        this.productService = props.productService;
        this.state={
            productList : []
        }
    }

    /**
     * It calls GET list product on server to render the list and subscribe to changes on newlist observable
     */
    componentDidMount(){
        //ask the server for the list
        this.getProductList(this);
        //set up the refreshing process to update product list when ProductEditor notify changes with rxjs
        this.productService.getNewList().subscribe((products:Product[])=>this.updateProductList(products,this));
    }

    /**
     * It calls GET list product on server and render it on this component
     * @param myThis reference to this component
     */
    private getProductList(myThis:this):void{
        //ask the server for the list
        this.productService.serverGetProducts().then((products:Product[])=>myThis.updateProductList(products,myThis));
    }

    /**
     * Renders that product list
     * @param products to render in the list of this component
     * @param myThis reference to this component
     */
    private updateProductList(products:Product[], myThis : this):void{
        myThis.setState({
            productList: products
        });
    }

    /**
     * Click event handler to trigger product edition process
     * It sends productI(product to edit) info to ProductEditor component
     * @param productI product to edit
     * @param myThis reference to this component 
     */
    private clickEditCreditCard(productI : Product, myThis : this):void{
        myThis.productService.addProductToEdit(productI);
    }

    /**
     * Click event handler to trigger product removal process
     * It calls server DELETE with the id of this product
     * @param productI product to delete
     * @param myThis reference to this component 
     */
    private clickDeleteCreditCard(productI : Product, myThis : this):void{
        let promiseResponseDelete = myThis.productService.serverDeleteProduct(productI.getId());
        promiseResponseDelete.then( function(deleted:boolean): void {
            if (deleted) {
                myThis.getProductList(myThis);
            }
        })
        .catch((error1: Error) => {
            console.error(error1.message);
        });
    }

    render(){
        return (
            <div className='card'>
                <div className='card-body'>
                    <h5 className='title'>Product List</h5>
                    <table className='table'>
                    <tbody data-testid='idTableProducts'>
                        {this.state.productList.map((productI : Product, index : number) => (
                            <tr key={'key'+index}>
                                <td>{fourLastChars(productI.getId())}</td>
                                <td>{productI.getName()}</td>
                                <td>{productI.getPrice().toString()}</td>
                                <td>{MyDateToString.dateToString(productI.getCreatedAt())}</td>
                                <td>{productI.getCategory().getName()}</td>
                                <td>{productI.getPicture()}</td>
                                <td>
                                    <FaPenSquare className='text-info' 
                                                data-testid={'edit' + index} onClick={() => {
                                                this.clickEditCreditCard(productI, this);
                                            }}/>
                                    <FaTrash className='text-danger' 
                                                data-testid={'delete' + index} onClick={() => {
                                                this.clickDeleteCreditCard(productI, this);
                                            }}/>                                    
                                </td>
                            </tr>
                        ))}
                    </tbody>
                    </table>
                </div>
            </div>
        );
    }
}