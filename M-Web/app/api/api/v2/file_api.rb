module Api::V2
  class FileApi < Grape::API
    namespace :files do
      # GET A FILE INFO
      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
      end
      get do
        token = params[:token]
        file = OpenStruct.new
        # GET A FILE INFO OF GOOGLE DRIVE
        if params[:type] == 'googledrive'
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
          file = session.file_by_id(params[:id])

          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          file.download_to_file("#{dir}/#{file.title}")
          url = request.base_url + '/files/' + current_user.id.to_s + '/' + file.title

          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data: {
              id: file.id,
              name: file.name,
              url: url,
              created_time: file.created_time,
              mime_type: file.mime_type,
              size: file.size
            }
          }
        # GET A FILE INFO OF DROPBOX
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          title = dbx.get_metadata(params[:id]).name
          size = dbx.get_metadata(params[:id]).size
          created_time = dbx.get_metadata(params[:id]).client_modified
          Down.download(dbx.get_temporary_link(params[:id])[1], destination: "#{dir}/#{title}")
          url = request.base_url + '/files/' + current_user.id.to_s + '/' + title
          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data: {
              id: params[:id],
              name: title,
              url: url,
              created_time: created_time,
              mime_type: nil,
              size: size
            }
          }
        # GET A FILE INFO OF BOX
        elsif params[:type] == 'box'
          client = Boxr::Client.new(token)
          file.id = params[:id]
          file.mime_type = 'file'
          file.name = client.file(params[:id]).name
          file.created_time = client.file(params[:id]).content_created_at
          file.size = client.file(params[:id]).size

          dir = File.dirname("#{Rails.root}/public/files/#{current_user.id}/file")
          FileUtils.mkdir_p(dir) unless File.directory?(dir)
          Down.download(client.download_url(params[:id]), destination: "#{dir}/#{title}")
          file['url'] = request.base_url + '/files/' + current_user.id.to_s + '/' + file['name']


          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Get File Successfully!'
          present :data, file, with: Api::Entities::FileEntity
        end
      end

      # UPLOAD A FILE TO CLOUD
      params do
        requires :id, type: String
        requires :file, type: File
        requires :type, type: String
        requires :token, type: String
      end
      post do
        parent = params[:id]
        token = params[:token]
        dir = params[:file][:tempfile].path
        title = params[:file][:filename].to_s
        type = params[:file][:type]

        # UPLOAD FILE TO GOOGLE DRIVE
        if params[:type] == 'googledrive'
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
          parent = session.file_by_id(parent)
          parent.upload_from_file(dir, title, convert: false)

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Upload Successfully!'
          present :data, nil
        # UPLOAD FILE TO DROPBOX
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          if params[:id] == 'root'
            dbx.upload('/' + title, File.open(dir, 'r'))
          else
            dbx.upload(params[:id] + '/' + title, File.open(dir, 'r'))
          end

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Upload Successfully!'
          present :data, nil
        # UPLOAD FILE TO BOX
        elsif params[:type] == 'box'
          client = Boxr::Client.new(token)
          client.upload_file(dir, parent, name: title)
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Upload Successfully!'
          present :data, nil
        end
      end

      # RENAME A FIELD IN CLOUD
      params do
        requires :id, type: String
        requires :name, type: String
        requires :type, type: String
        requires :token, type: String
      end
      put do
        token = params[:token]
        name = params[:name]

        # RENAME A FILE IN GOOGLE DRIVE
        if params[:type] == 'googledrive'
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

            file = session.file_by_id(params[:id])
            file.rename(name)

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Update Successfully!'
            present :data, nil
        # RENAME A FILE IN DROPBOX
        elsif params[:type] == 'dropbox'
            dbx = Dropbox::Client.new(token)
            parent = File.dirname(params[:id])

            dbx.move(params[:id], parent.to_s + name.to_s)

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Update Successfully!'
            present :data, nil
        # RENAME A FILE IN BOX
        elsif params[:type] == 'box'
            client = Boxr::Client.new(token)
            client.update_file(params[:id], name: name)

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Delete Successfully!'
            present :data, nil
        end
      end



      # DELETE A FIELD IN CLOUD
      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
      end
      delete do
        token = params[:token]
        # DELETE A FILE IN GOOGLE DRIVE
        if params[:type] == 'googledrive'
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

          file = session.file_by_id(params[:id])
          file.delete(true)

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Delete Successfully!'
          present :data, nil
        # DELETE A FILE IN DROPBOX
        elsif params[:type] == 'dropbox'
          dbx = Dropbox::Client.new(token)
          dbx.delete(params[:id])

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Delete Successfully!'
          present :data, nil
        # DELETE A FILE IN BOX
        elsif params[:type] == 'box'
            client = Boxr::Client.new(token)
            client.delete_file(params[:id])

            present :time, Time.now.to_s
            present :status, 'success'
            present :message, 'Delete Successfully!'
            present :data, nil
        end
      end
    end
  end
end
