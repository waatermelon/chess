package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{

    ArrayList<AuthData> db;


    public MemoryAuthDAO() {
        db = new ArrayList<AuthData>();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        System.out.println("creating auth");
        for (AuthData dbAuthData: db) {
            System.out.println(authData);
            if (dbAuthData.equals(authData)) {
                throw new DataAccessException("");
            }
        }
        db.add(authData);
    }

    // Read
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData dbAuthData: db) {
            if (dbAuthData.authToken().equals(authToken)) {
                return dbAuthData;
            }
        }
        throw new DataAccessException("Token was not found.");
    }

    // Update/Delete
    @Override
    public void deleteAuth(AuthData authData) {
        for (int i = 0; i < db.size(); ++i) {
            if (db.get(i).equals(authData)) {
                db.remove(i);
                return;
            }
        }
    }

    // Delete
    @Override
    public void clear() {
        db.clear();
    }
}
