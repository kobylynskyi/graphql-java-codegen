package io.github.kobylynskyi.bikeshop.graphql.mappers;

import io.github.kobylynskyi.bikeshop.graphql.model.BikeInputTO;
import io.github.kobylynskyi.bikeshop.graphql.model.BikeTO;
import io.github.kobylynskyi.bikeshop.graphql.model.BikeTypeTO;
import io.github.kobylynskyi.bikeshop.model.Bike;
import io.github.kobylynskyi.bikeshop.model.BikeType;
import java.math.BigDecimal;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-09-17T13:44:19-0500",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_162 (Oracle Corporation)"
)
@Component
public class BikeMapperImpl implements BikeMapper {

    @Override
    public BikeTO map(Bike from) {
        if ( from == null ) {
            return null;
        }

        BikeTO bikeTO = new BikeTO();

        bikeTO.setId( from.getId() );
        bikeTO.setType( bikeTypeToBikeTypeTO( from.getType() ) );
        bikeTO.setBrand( from.getBrand() );
        bikeTO.setSize( from.getSize() );
        bikeTO.setYear( from.getYear() );
        if ( from.getPrice() != null ) {
            bikeTO.setPrice( from.getPrice().toString() );
        }
        bikeTO.setAddedDateTime( from.getAddedDateTime() );

        return bikeTO;
    }

    @Override
    public Bike mapInput(BikeInputTO from) {
        if ( from == null ) {
            return null;
        }

        Bike bike = new Bike();

        bike.setType( mapInputType( from.getType() ) );
        bike.setBrand( from.getBrand() );
        bike.setSize( from.getSize() );
        bike.setYear( from.getYear() );
        if ( from.getPrice() != null ) {
            bike.setPrice( new BigDecimal( from.getPrice() ) );
        }

        return bike;
    }

    @Override
    public BikeType mapInputType(BikeTypeTO type) {
        if ( type == null ) {
            return null;
        }

        BikeType bikeType;

        switch ( type ) {
            case ROAD: bikeType = BikeType.ROAD;
            break;
            case TOURING: bikeType = BikeType.TOURING;
            break;
            case TRIAL: bikeType = BikeType.TRIAL;
            break;
            case TRACK: bikeType = BikeType.TRACK;
            break;
            case MOUNTAIN: bikeType = BikeType.MOUNTAIN;
            break;
            case HYBRID: bikeType = BikeType.HYBRID;
            break;
            case BMX: bikeType = BikeType.BMX;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + type );
        }

        return bikeType;
    }

    protected BikeTypeTO bikeTypeToBikeTypeTO(BikeType bikeType) {
        if ( bikeType == null ) {
            return null;
        }

        BikeTypeTO bikeTypeTO;

        switch ( bikeType ) {
            case ROAD: bikeTypeTO = BikeTypeTO.ROAD;
            break;
            case TOURING: bikeTypeTO = BikeTypeTO.TOURING;
            break;
            case TRIAL: bikeTypeTO = BikeTypeTO.TRIAL;
            break;
            case TRACK: bikeTypeTO = BikeTypeTO.TRACK;
            break;
            case MOUNTAIN: bikeTypeTO = BikeTypeTO.MOUNTAIN;
            break;
            case HYBRID: bikeTypeTO = BikeTypeTO.HYBRID;
            break;
            case BMX: bikeTypeTO = BikeTypeTO.BMX;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + bikeType );
        }

        return bikeTypeTO;
    }
}
