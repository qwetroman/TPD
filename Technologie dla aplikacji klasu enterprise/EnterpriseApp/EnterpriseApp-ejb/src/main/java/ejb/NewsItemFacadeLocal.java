/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import java.util.List;
import jakarta.ejb.Local;

/**
 *
 * @author Danylo
 */
@Local
public interface NewsItemFacadeLocal {
    public List<NewsItem> getAllNewsItems();
}