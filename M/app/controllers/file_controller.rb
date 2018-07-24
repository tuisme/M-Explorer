class FileController < ApplicationController
  before_action :oauth_set, only: [:index]
  def index
    @auth_url = @credentials.authorization_uri.to_s
    @credentials.refresh_token = params[:token]
    @credentials.fetch_access_token!
    session = GoogleDrive::Session.from_credentials(@credentials)


    temp = session.file_by_id(params[:id])
     if temp.mime_type == 'application/vnd.google-apps.folder'
       @files = temp.files
    else
       file = temp
       dir = File.dirname("#{Rails.root}/public/files/#{current_user.id.to_s}/file")
       FileUtils.mkdir_p(dir) unless File.directory?(dir)
       file.download_to_file("#{dir}/#{file.title}")
       url = "#{request.protocol + request.host + ":" + request.port.to_s}/files/#{current_user.id.to_s}/#{file.title}"
       redirect_to open_path(url: Base64.encode64(url))
    end
  end

  def open
    @url = Base64.decode64(params[:url])
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
      "prompt" => "consent",
      "include_granted_scopes" => "true"

    },
    redirect_uri: "#{request.protocol + request.host + ":" + request.port.to_s}/googledrive_redirect")
  end
end
