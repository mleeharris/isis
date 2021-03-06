package demoapp.dom.types.javasql.javasqldate.jdo;

import java.sql.Date;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.testing.fixtures.applib.fixturescripts.FixtureScript;

import demoapp.dom._infra.seed.SeedServiceAbstract;
import demoapp.dom.types.Samples;

@Service
public class JavaSqlDateJdoSeedService extends SeedServiceAbstract {

    public JavaSqlDateJdoSeedService() {
        super(JavaSqlDateJdoEntityFixture::new);
    }

    static class JavaSqlDateJdoEntityFixture extends FixtureScript {

        @Override
        protected void execute(ExecutionContext executionContext) {
            samples.stream()
                    .map(JavaSqlDateJdo::new)
                   .forEach(domainObject -> {
                        repositoryService.persist(domainObject);
                        executionContext.addResult(this, domainObject);
                    });
        }

        @Inject
        RepositoryService repositoryService;

        @Inject
        Samples<Date> samples;
    }
}
