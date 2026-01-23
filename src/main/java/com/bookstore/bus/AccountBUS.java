package com.bookstore.bus;

import com.bookstore.dao.AccountDAO;
import com.bookstore.dao.EmployeeDAO;
import com.bookstore.dto.AccountDTO;
import com.bookstore.dto.EmployeeDTO;

public class AccountBUS {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public EmployeeDTO login(String username, String password) throws Exception {
        AccountDTO acc = accountDAO.selectByUsername(username);

        if (acc == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }

        if (acc.getStatus() == 0) {
            throw new Exception("Tài khoản này đã bị khóa. Vui lòng liên hệ Quản Lý!");
        }

        if (!acc.getPassword().equals(password)) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        return employeeDAO.selectById(acc.getEmployeeId());
    }
}