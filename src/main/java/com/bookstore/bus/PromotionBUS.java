package com.bookstore.bus;

import com.bookstore.dao.PromotionDAO;
import com.bookstore.dto.PromotionDTO;
import java.util.List;

public class PromotionBUS {
    private PromotionDAO promotionDAO = new PromotionDAO();

    public List<PromotionDTO> getAllValidPromotions() {
        return promotionDAO.selectAllValidPromotions();
    }

    public double getPromotionPercentByBook(int bookId) {
        return promotionDAO.getPromotionPercentByBook(bookId);
    }
}