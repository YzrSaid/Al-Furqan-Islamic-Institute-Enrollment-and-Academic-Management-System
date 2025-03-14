package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.Repositories.DiscountRepo;
import com.example.testingLogIn.Repositories.StudentDiscountRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountsServices {

    private final DiscountRepo discRepo;
    private final StudentDiscountRepo studDiscRepo;
    private final StudentRepo studRepo;

    public DiscountsServices(DiscountRepo discRepo, StudentDiscountRepo studDiscRepo, StudentRepo studRepo) {
        this.discRepo = discRepo;
        this.studDiscRepo = studDiscRepo;
        this.studRepo = studRepo;
    }

    public boolean addDiscount(Discount discount){
        if(!discRepo.findDiscountByName(discount.getDiscountName()).isEmpty())
            return false;
        discRepo.save(discount);

        return true;
    }
}
