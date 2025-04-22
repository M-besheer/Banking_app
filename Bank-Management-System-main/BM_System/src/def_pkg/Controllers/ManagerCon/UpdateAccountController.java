package def_pkg.Controllers.ManagerCon;

import def_pkg.*;

public class UpdateAccountController {

    private Manager manager;

    public void setManagerData(Manager manager) {
        System.out.println("Manager is: " + manager.getName());
        this.manager = manager;
    }
}
