package Suppliers.dataaccess.DAO;

import Suppliers.DTO.AgreementDTO;

import java.sql.SQLException;
import java.util.List;

public class JdbcAgreementDAO implements IAgreementDAO {
    @Override
    public void insert(AgreementDTO dto) throws SQLException {

    }

    @Override
    public void update(AgreementDTO dto) throws SQLException {

    }

    @Override
    public void deleteById(int agreementId) throws SQLException {

    }

    @Override
    public AgreementDTO getById(int agreementId) throws SQLException {
        return null;
    }

    @Override
    public List<AgreementDTO> getAll() throws SQLException {
        return List.of();
    }
}
