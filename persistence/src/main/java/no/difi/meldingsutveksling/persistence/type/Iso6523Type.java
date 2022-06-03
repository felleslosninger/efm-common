package no.difi.meldingsutveksling.persistence.type;

import no.difi.meldingsutveksling.domain.Iso6523;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class Iso6523Type extends AbstractSingleColumnStandardBasicType<Iso6523>
        implements DiscriminatorType<Iso6523> {

    public static final Iso6523Type INSTANCE = new Iso6523Type();

    public Iso6523Type() {
        super(VarcharTypeDescriptor.INSTANCE, Iso6523TypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "string";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(Iso6523 value, Dialect dialect) {
        return Iso6523TypeDescriptor.INSTANCE.toString(value);
    }

    @Override
    public Iso6523 stringToObject(String string) {
        return Iso6523TypeDescriptor.INSTANCE.fromString(string);
    }
}