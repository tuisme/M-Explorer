class ApplicationController < ActionController::Base
  # protect_from_forgery with: :exception
  # before_action :configure_permitted_parameters, if: :devise_controller?
  # before_action :authenticate_user!, except: [ :about, :contact]
  # before_action :oauth_set, only: [:home,:googledrive_redirect]

  def home
    # @auth_url = @credentials.authorization_uri.to_s
    #
    # @clouds = current_user.clouds
  end

  def about
  end

  def googledrive_redirect
    @credentials.code = params[:code]
    cname = params[:cname]
    token = @credentials.fetch_access_token!
    session = GoogleDrive::Session.from_credentials(@credentials)
    root = session.root_collection.id.to_s

    refresh_token = @credentials.refresh_token


    current_user.clouds.create(croot: root,cname: cname, ctype: 'googledrive',ctoken:  refresh_token)

    redirect_to root_path, notice: 'Success!'
  end

  private
  def oauth_set
    @credentials = Google::Auth::UserRefreshCredentials.new(
    client_id: "389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com",
    client_secret: "zhKkS-8vI_RNqReXOjAx4c5r",
    scope: [
      "https://www.googleapis.com/auth/drive"
    ],
    additional_parameters: {
      "access_type" => "offline",
      "prompt" => "consent"

    },
    redirect_uri: "#{request.protocol + request.host + ":" + request.port.to_s}/googledrive_redirect")
  end

  protected
  def configure_permitted_parameters
    devise_parameter_sanitizer.permit(:sign_up, keys: [:first_name, :last_name])
    devise_parameter_sanitizer.permit(:account_update, keys: [:first_name, :last_name])
  end
end
