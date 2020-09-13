package context;

/**
 * @author abiral
 * created on 10/13/20
 *  This stores all the activation related data
 *  and methods that will let us control the application
 */

public class ApplicationContext {


    private ApplicationContext() {
    }

    public static ApplicationContext getInstance(){
        return new ApplicationContext();
    }

}
