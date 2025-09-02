package pe.joedayz.estore.transfer.service;

import pe.joedayz.estore.transfer.model.TransferRestModel;

public interface TransferService {
    public boolean transfer(TransferRestModel productPaymentRestModel);
}
