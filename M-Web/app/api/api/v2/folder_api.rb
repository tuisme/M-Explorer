module GoogleUploadHelper
  def google_upload(_folder = nil, access_token = nil, json = nil)
    @credentials = Google::Auth::UserRefreshCredentials.new(
      client_id: '389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com',
      client_secret: 'zhKkS-8vI_RNqReXOjAx4c5r',
      scope: [
        'https://www.googleapis.com/auth/drive'
      ],
      additional_parameters: {
        'access_type' => 'offline',
        'prompt' => 'consent'
      },
      redirect_uri: "http://localhost:3000")

    @credentials.refresh_token = access_token
    access_token = @credentials.fetch_access_token!['access_token']
    session = GoogleDrive::Session.from_credentials(@credentials)

    parent = session.file_by_id(_folder)

    json.each_with_index do |up, index|
      if index == 0
        parent = parent.create_subcollection(up['parent'])
      elsif up.is_a?(Array)
        google_upload(parent.id, access_token, up)
      else
        parent.upload_from_file("/home/kyle/Project/M-Explorer/M-Web/public/" + up['path'], up['name'], convert: false)
      end
    end
  end
end
module Api::V2
  class FolderApi < Grape::API
    helpers GoogleUploadHelper
    namespace :folders do
      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
      end

      get do
        token = params[:token]
        if    params[:type] == 'googledrive'
          @credentials = Google::Auth::UserRefreshCredentials.new(
            client_id: '389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com',
            client_secret: 'zhKkS-8vI_RNqReXOjAx4c5r',
            scope: [
              'https://www.googleapis.com/auth/drive'
            ],
            additional_parameters: {
              'access_type' => 'offline',
              'prompt' => 'consent'
            },
            redirect_uri: 'http://localhost:3000')

          @credentials.refresh_token = token
          @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)

          temp = session.file_by_id(params[:id])
          if temp.mime_type == 'application/vnd.google-apps.folder'
            @files = temp.files

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, nil
            present :data, @files, with: Api::Entities::GoogleDriveFolderEntity

          end
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          files = if params[:id] == 'root'
                    dbx.list_folder('')
                  else
                    dbx.list_folder(params[:id])
                  end

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, nil
          present :data, files.entries, with: Api::Entities::DropboxFolderEntity
        elsif params[:type] == 'box'
          client = Boxr::Client.new(token)
          files = client.folder_items(params[:id])

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, nil
          present :data, files.entries, with: Api::Entities::BoxFolderEntity
        end
      end

      params do
        requires :name, type: String
        requires :parent, type: String
        requires :type, type: String
        requires :token, type: String
      end

      post do
        token = params[:token]
        if    params[:type] == 'googledrive'
          @credentials = Google::Auth::UserRefreshCredentials.new(
            client_id: '389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com',
            client_secret: 'zhKkS-8vI_RNqReXOjAx4c5r',
            scope: [
              'https://www.googleapis.com/auth/drive'
            ],
            additional_parameters: {
              'access_type' => 'offline',
              'prompt' => 'consent'
            },
            redirect_uri: 'http://localhost:3000')

          @credentials.refresh_token = token
          token = @credentials.fetch_access_token!
          session = GoogleDrive::Session.from_credentials(@credentials)

          parent = session.file_by_id(params[:parent])
          folder = parent.create_subcollection(params[:name])

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Create Folder Successfully!'
          present :data, folder, with: Api::Entities::GoogleDriveFolderEntity
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          if params[:parent] == 'root'
            dbx.create_folder('/' + params[:name])
          else
            dbx.create_folder(params[:parent] + '/' + params[:name])
          end
        elsif params[:type] == 'box'
          client = Boxr::Client.new(token)
          client.create_folder(params[:name], params[:parent])
        end
      end

      params do
        requires :id, type: String
        requires :json, type: String
        requires :zip, type: File
        requires :type, type: String
        requires :token, type: String
      end
      post 'zip' do
        parsed_json = JSON.parse(params[:json])

        FileUtils.mkdir_p('public/' + parsed_json['root'][0]['parent'])
        Zip::File.open(params[:zip][:tempfile].path) do |zip_file|
          zip_file.each do |f|
            fpath = File.join('public/'+ parsed_json['root'][0]['parent'], f.name)
            zip_file.extract(f, fpath) unless File.exist?(fpath)
          end
        end

        google_upload(params[:id], params[:token], parsed_json['root'])
        
        present :time, Time.now.to_s
        present :status, 'success'
        present :message, nil
        present :data, nil

      end
    end
  end
end
