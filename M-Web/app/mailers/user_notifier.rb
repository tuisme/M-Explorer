class UserNotifier < ApplicationMailer

  # Subject can be set in your I18n file at config/locales/en.yml
  # with the following lookup:
  #
  #   en.user_mailer.regitration_confirmation.subject
  #
  def regitration_confirmation(user)
    @user = user

    mail to: @user.email, subject: "Email kích hoạt tài khoản"
  end
end
