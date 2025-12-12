package no.difi.meldingsutveksling.persistence.type;

import no.difi.meldingsutveksling.domain.Iso6523;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;


@SuppressWarnings("unused")
public class Iso6523TypeDescriptor extends AbstractClassJavaType<Iso6523> {
    public static final Iso6523TypeDescriptor INSTANCE = new Iso6523TypeDescriptor();

    public Iso6523TypeDescriptor() {
        super(Iso6523.class);
    }

    @Override
    public String toString(Iso6523 value) {
        return value.toString();
    }

    public Iso6523 fromString(String string) {
        return Iso6523.parse(string);
    }

    @Override
    public int extractHashCode(Iso6523 value) {
        return value.hashCode();
    }

    @Override
    public boolean areEqual(Iso6523 one, Iso6523 another) {
        return one == another || (one != null && another != null && one.compareTo(another) == 0);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <X> X unwrap(Iso6523 value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Iso6523.class.isAssignableFrom(type)) {
            return (X) value;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) value.toString();
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> Iso6523 wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof String s) {
            return Iso6523.parse(s);
        }
        throw unknownWrap(value.getClass());
    }
}
