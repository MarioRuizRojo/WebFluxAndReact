import React from 'react';
import './App.css';

import ProductEditor from './components/ProductEditor';
import ProductList from './components/ProductList';
import { ProductService } from './services/ProductService';
import { Service } from './services/Service';
import { ProductServiceDebug } from './services/ProductServiceDebug';
import { debug } from './constants';

/**
 * Main React App with bootstrap classes
 * It renders one product editor and one product list
 */
function App() {
    let productService1 : Service;
    if(debug)
        productService1 = new ProductServiceDebug();
    else
        productService1 = new ProductService();
    const myProps : any = {productService:productService1};
  return (
    <div className='container mt-5'>
        <div className='row'>
        <div className='col-lg-8 offset-lg-2'>
            <div className='card'>
            <div className='card-body'>
                <h1 className='title'>Products in Dollars App</h1>
            </div>
            </div>
        </div>
        </div>
        <div className='row mt-4'>
        <div className='col-lg-6'>
            <ProductEditor {...myProps}/>
        </div>
        <div className='col-lg-6'>
            <ProductList {...myProps}/>
        </div>
        </div>
    </div>
  );
}

export default App;
