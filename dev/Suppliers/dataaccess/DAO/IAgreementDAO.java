package Suppliers.dataaccess.DAO;

import Suppliers.DTO.AgreementDTO;

import java.sql.SQLException;
import java.util.List;

public interface IAgreementDAO {

    void insert(AgreementDTO dto) throws SQLException;

    void update(AgreementDTO dto) throws SQLException;

    void deleteById(int agreementId) throws SQLException;

    AgreementDTO getById(int agreementId) throws SQLException;

    List<AgreementDTO> getAll() throws SQLException;
}
