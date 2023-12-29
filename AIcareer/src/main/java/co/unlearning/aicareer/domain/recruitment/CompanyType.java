package co.unlearning.aicareer.domain.recruitment;

public enum CompanyType{
    STARTUP, MAJOR, UNICORN, MIDDLE;

    public static CompanyType ofCompanyType(String type) throws IllegalAccessException {
        for(CompanyType companyType : CompanyType.values()) {
            if (companyType.toString().equals(type)) {
                return companyType;
            }
        }
        throw new IllegalAccessException();
    }
}
