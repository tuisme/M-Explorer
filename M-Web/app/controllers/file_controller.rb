class FileController < ApplicationController
  before_action :oauth_set, only: [:index, :open]
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
    # @url = Base64.decode64(params[:url])

    # dbx = Dropbox::Client.new("EMuhh18IKQAAAAAAAAAgelA7wkZ8OcvU2uXwTZzH0y-0GjI1NQXzvV2sv3ZLDfsF")
    # @credentials.refresh_token = "1/w-fG1yj9f936nqUsGvj6UHbITfP64plO5ZLWAVPuuIE"
    # @credentials.fetch_access_token!
    # session = GoogleDrive::Session.from_credentials(@credentials)
    # d


    # render plain: parsed_json['refresh_token']



    # @credentials.refresh_token = "1/UJjjZ1MdDbT4iHNpWsx8FPMqCAUIT16HNyqHH2u23hQ"
    # @credentials.fetch_access_token!
    # session = GoogleDrive::Session.from_credentials(@credentials)
    gg
    # rf

    # render plain: ActionController::Base.helpers.asset_path('/images/default-avatar.png')

    # render plain: dir = "#{Rails.root}/public/files/1/JPG.jpg"
    # session.upload_from_file(dir,"aaa.aa")
    # dbx = Dropbox::Client.new("EMuhh18IKQAAAAAAAAAgiEEHMnyL_dc-kxnQZ6BT8TqtOaqpZFt8rw-iJ7xxuB8z")
    #
    # files = dbx.list_folder("")
    # f





    # a = Boxr::refresh_tokens("NVM9y8PR1yPiRTzhiTZMAs8IBazZGuUoIUm6qwaSDUYt5IwqCiEbXLSvtm3EWWDn", client_id: "i9jieqavbpuutnbbrqdyeo44m0imegpk", client_secret: "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F")
    # ss

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
