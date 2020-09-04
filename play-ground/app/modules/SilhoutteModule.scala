package modules

import com.mohiva.play.silhouette
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import com.mohiva.play.silhouette.api.services.IdentityService
import models._
import services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.impl.util._
import com.mohiva.play.silhouette.api.{Silhouette, Environment, EventBus}
import scala.concurrent.ExecutionContext.Implicits.global
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.api.SilhouetteProvider
import utils.auth.DefaultEnv
import com.google.inject.Provides
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.impl.authenticators._
import play.api.Configuration
import play.api.mvc.SessionCookieBaker
import com.mohiva.play.silhouette.api.crypto.AuthenticatorEncoder
import com.mohiva.play.silhouette.api.crypto.Base64AuthenticatorEncoder
import play.api.mvc.DefaultSessionCookieBaker
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.EnumerationReader._
import net.ceedubs.ficus.readers.ValueReader
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import daos.PasswordInfoDAO
import play.api.db.slick.DatabaseConfigProvider

class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[AccountService].to[AccountServiceImpl]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator())
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
    bind[AuthenticatorEncoder].toInstance(new Base64AuthenticatorEncoder)
  }

  @Provides
  def provideEnvironment(
      accountService: AccountService,
      authenticatorService: AuthenticatorService[SessionAuthenticator],
      eventBus: EventBus
  ): Environment[DefaultEnv] =
    Environment[DefaultEnv](
      accountService,
      authenticatorService,
      Seq(),
      eventBus
    )

  @Provides
  def providePasswordInfo(
      dbConfig: DatabaseConfigProvider
  ): DelegableAuthInfoDAO[PasswordInfo] = {
    new PasswordInfoDAO(dbConfig)
  }

  /**
    * Provides the auth info repository.
    *
    * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
    * @return The auth info repository instance.
    */
  @Provides
  def provideAuthInfoRepository(
      passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]
  ): AuthInfoRepository = {

    new DelegableAuthInfoRepository(
      passwordInfoDAO
    )
  }

  @Provides
  def provideAuthenticatorService(
      configuration: Configuration,
      fingerPrintGenerator: FingerprintGenerator,
      authEncoder: AuthenticatorEncoder,
      sessionCookieBaker: SessionCookieBaker,
      clock: Clock
  ): AuthenticatorService[SessionAuthenticator] = {
    val config = configuration.underlying.as[SessionAuthenticatorSettings](
      "silhouette.authenticator"
    )
    new SessionAuthenticatorService(
      config,
      fingerPrintGenerator,
      authEncoder,
      sessionCookieBaker,
      clock
    )
  }

  /**
    * Provides the password hasher registry.
    *
    * @return The password hasher registry.
    */
  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry = {
    PasswordHasherRegistry(
      new BCryptSha256PasswordHasher(),
      Seq(new BCryptPasswordHasher())
    )
  }

  /**
    * Provides the credentials provider.
    *
    * @param authInfoRepository     The auth info repository implementation.
    * @param passwordHasherRegistry The password hasher registry.
    * @return The credentials provider.
    */
  @Provides
  def provideCredentialsProvider(
      authInfoRepository: AuthInfoRepository,
      passwordHasherRegistry: PasswordHasherRegistry
  ): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }
}
