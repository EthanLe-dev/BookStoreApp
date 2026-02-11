package com.bookstore.bus;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.dto.CustomerDTO;

public class CustomerBUS {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public CustomerDTO selectByPhone(String phone) {
        return customerDAO.selectByPhone(phone);
    }
}
