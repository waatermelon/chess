package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO(ArrayList<AuthData> initAuthData) {
        db = initAuthData;
    }

    public SQLAuthDAO() {
        db = new ArrayList<AuthData>();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        for (AuthData dbAuthData: db) {
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
