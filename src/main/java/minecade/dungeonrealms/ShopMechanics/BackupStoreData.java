package minecade.dungeonrealms.ShopMechanics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.database.ConnectionPool;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class BackupStoreData extends BukkitRunnable {

	public static boolean shutdown = false;
	
    public void run() {
        if (ShopMechanics.need_sql_update.size() > 0) {
            // Upload it all.
            for (String shop_owner : ShopMechanics.need_sql_update) {
                String shop_contents = "";
                String collection_bin_s = "null";

                if (!(ShopMechanics.collection_bin.containsKey(shop_owner))) {
                    collection_bin_s = "null";
                } else if (ShopMechanics.collection_bin.containsKey(shop_owner)) {
                    collection_bin_s = Hive.convertInventoryToString(shop_owner, ShopMechanics.collection_bin.get(shop_owner), false);
                }

                if (!(ShopMechanics.shop_stock.containsKey(shop_owner))) {
                    shop_contents = "";
                    // We want to set the shop_backup to 'empty' in the SQL.
                } else if (ShopMechanics.shop_stock.containsKey(shop_owner)) {
                    shop_contents = Hive.convertInventoryToString(shop_owner, ShopMechanics.shop_stock.get(shop_owner), false);
                }

                /*
                 * if(shop_contents.equalsIgnoreCase("")){ shop_contents = "null"; }
                 */
                Connection con = null;
                PreparedStatement pst = null;

                try {
                	String query = "INSERT INTO shop_database (p_name, shop_backup, collection_bin)" + " VALUES" + "('" + shop_owner + "', '"
                            + StringEscapeUtils.escapeSql(shop_contents) + "', '" + StringEscapeUtils.escapeSql(collection_bin_s)
                            + "') ON DUPLICATE KEY UPDATE shop_backup = '" + StringEscapeUtils.escapeSql(shop_contents) + "', collection_bin='"
                            + StringEscapeUtils.escapeSql(collection_bin_s) + "';";
                    pst = ConnectionPool.getConnection().prepareStatement(query);
                    pst.executeUpdate();

                } catch (SQLException ex) {
                    ex.printStackTrace();

                } finally {
                    try {
                        if (pst != null) {
                            pst.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

                ShopMechanics.need_sql_update.remove(shop_owner);
            }
            // ShopMechanics.need_sql_update.clear();
        }
        
        if(shutdown) shutdown = false;
    }
}
