package pe.joedayz.productsmicroservice.service;

import pe.joedayz.productsmicroservice.rest.CreateProductRestModel;


public interface ProductService {
    String createProduct(CreateProductRestModel productRestModel) throws Exception;
}
